package com.azure.storage.storageaccountsample.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.azure.spring.autoconfigure.storage.resource.AzureStorageResourcePatternResolver;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;

/**
 * Azure StorageAccountヘルパークラス。
 */
@Component
public class StorageAccountHelper {

	/**
	 * リソースローダ。
	 */
	@Autowired
	ResourceLoader resourceLoader;

	/**
	 * リソースパタンリゾルバ。
	 */
	ResourcePatternResolver resourcePatternResolver;

	/**
	 * Azureストレージアカウントの名前。
	 */
	@Value("${azure.storage.account-name}")
	String accountName;

	/**
	 * Azureストレージアカウントのアクセスキー。
	 */
	@Value("${azure.storage.account-key}")
	String accountKey;

	/**
	 * S3クライアント。
	 */
//    @Inject
//    AmazonS3 s3client;

	/**
	 * S3バケットを検索するためリソースパタンリゾルバをSpring Cloud AWSでラップする。
	 * 
	 * @param applicationContext
	 * @param amazonS3
	 */
//    @Inject
//    public void setupResolver(ApplicationContext applicationContext,
//            AmazonS3 amazonS3) {
//        this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, applicationContext);
//    }

	/**
	 * 単一ファイルのアップロードを行う。
	 * 
	 * @param uploadFile    アップロードファイル
	 * @param containerName コンテナ名
	 * @param putDirectory  アップロードディレクトリ
	 * @param fileName      ファイル名
	 */
	public void fileUpload(InputStream uploadFile, String containerName, String putDirectory, String fileName) {
		WritableResource resource = getResource(containerName, putDirectory, fileName);
		try (OutputStream out = resource.getOutputStream()) {
//			String fileContent = StreamUtils.copyToString(new ClassPathResource("sample.txt").getInputStream(), Charset.defaultCharset()  );
//			out.write(fileContent.getBytes());
			// アップロードファイルの情報をOutputStreamへコピー
			copy(uploadFile, out);
		} catch (IOException e) {
			// TODO ログ出力の実装を作成する

		}
	}

	/**
	 * 単一ファイルのダウンロードを行う。
	 * 
	 * @param downloadFile  ダウンロードファイル
	 * @param containerName コンテナ名
	 * @param putDirectory  ダウンロードディレクトリ
	 * @param fileName      ファイル名
	 * @throws Exception
	 */
	public void fileDownload(OutputStream downloadFile, String containerName, String putDirectory, String fileName) {
		Resource resource = getResource(containerName, putDirectory, fileName);
		try (InputStream in = resource.getInputStream()) {
			copy(in, downloadFile);
		} catch (IOException e) {
			// TODO ログ出力の実装を作成する
		}
	}

	/**
	 * 単一ファイルの削除を行う。
	 * 
	 * @param containerName   コンテナ名
	 * @param targetDirectory 削除先ディレクトリ
	 * @param fileName        ファイル名
	 */
	public void fileDelete(String containerName, String targetDirectory, String fileName) {

		BlockBlobClient blobClient = createBlockBlobClient(containerName, targetDirectory, fileName);
		blobClient.delete();
	}

    /**
     * 複数ファイルの削除を行う。
     * @param containerName　コンテナ名
     * @param deleteKeyList 削除対象キーリスト
     */
    public void multiFileDelete(String containerName, String targetDirectory, List<String> fileList) {
        for (String deleteFileName : fileList) {
        	String replacedDeleteFileName = deleteFileName.replaceFirst(targetDirectory, "");
        	System.out.println(replacedDeleteFileName);
//        	fileDelete(containerName, targetDirectory, replacedDeleteFileName);
        }
    }

	/**
	 * 単一ファイルのコピーを行う。
	 * 
	 * @param sourceContainerName コピー元コンテナ名
	 * @param sourceDirectory     コピー元ディレクトリ
	 * @param sourceFileName      コピー元ファイル名
	 * @param targetContainerName コピー先コンテナ名
	 * @param targetDirectory     コピー先ディレクトリ
	 * @param targetFileName      コピー先ファイル名
	 * @throws IOException
	 */
	public void fileCopy(String sourceContainerName, String sourceDirectory, String sourceFileName,
			String targetContainerName, String targetDirectory, String targetFileName) {
		Resource source = getResource(sourceContainerName, sourceDirectory, sourceFileName);
		WritableResource target = getResource(targetContainerName, targetDirectory, targetFileName);
		try (InputStream in = source.getInputStream(); OutputStream out = target.getOutputStream()) {
			copy(in, out);
		} catch (IOException e) {
			// TODO ログ出力の実装を作成する
		}
	}

	/**
	 * AzureStorageAccountURIの取得を行う。
	 * 
	 * @param resource StorageAccountリソース
	 * @return
	 */
	public URI getAzureStorageAccountURI(Resource resource) {
		try {
			return resource.getURI();
		} catch (IOException e) {
			// TODO ログ出力の実装を作成する
			return null;
//            throw new SystemException(LogMessages.E_AR_A0_L9003
//                    .getCode(), LogMessages.E_AR_A0_L9003.getMessage(), e);
		}
	}

	/**
	 * ファイルの検索を行う。
	 * 
	 * @param containerName 検索対象コンテナ名
	 * @param directory     検索対象ディレクトリ
	 * @param pattern       ファイル名パターン
	 * @return
	 * @throws IOException
	 */
	public Resource[] fileSearch(String containerName, String directory, String pattern) {
		
		StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential)
				.buildClient();
		resourcePatternResolver = new AzureStorageResourcePatternResolver(storageClient);
		try {
			return resourcePatternResolver.getResources("azure-blob://" + containerName + "/" + directory + pattern);
		} catch (IOException e) {
			// TODO ログ出力の実装を作成する
			return null;
//            throw new SystemException(LogMessages.E_AR_A0_L9003
//                    .getCode(), LogMessages.E_AR_A0_L9003.getMessage(
//                            bucketName), e);
		}

	}

	/**
	 * WritableResourceを取得する。
	 * 
	 * @param containerName コンテナ名
	 * @param putDirectory  アップロードディレクトリ
	 * @param fileName      ファイル名
	 * @return WritableResource
	 */
	public WritableResource getResource(String containerName, String putDirectory, String fileName) {

		URI tempUri = null;

		try {
			tempUri = new URI("azure-blob://" + containerName + "/" + putDirectory + fileName);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        AmazonS3URI tempUri = new AmazonS3URI(AWSConstants.S3_PROTOCOL_PREFIX
//                .getConstants() + bucketName + "/" + putDirectory + fileName);
		return (WritableResource) resourceLoader.getResource(tempUri.toString());
	}

	/**
	 * Streamのコピーを行う。
	 * 
	 * @param in  入力データ
	 * @param out 出力データ
	 * @throws IOException
	 */
	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buff = new byte[1024];
		for (int len = in.read(buff); len > 0; len = in.read(buff)) {
			out.write(buff, 0, len);
		}
	}

	/**
	 * BlockBlobClientを作成する。
	 * 
	 * @param containerName   バケット名
	 * @param targetDirectory 削除先ディレクトリ
	 * @param fileName        ファイル名
	 */
	private BlockBlobClient createBlockBlobClient(String containerName, String targetDirectory, String fileName) {

		StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);

		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential)
				.buildClient();
		// コンテナ名を指定してBlobContainerClientを生成
		BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient(containerName);
		// BLOBのディレクトリ名+ファイル名を指定してBlockBlobClientを生成
		return blobContainerClient.getBlobClient(targetDirectory + fileName).getBlockBlobClient();
	}
	
//	public void generateSas(String containerName, String targetDirectory, String fileName) {
//		OffsetDateTime myExpiryTime = OffsetDateTime.now().plusDays(1);
//		 BlobContainerSasPermission myPermission = new BlobContainerSasPermission().setReadPermission(true);
//
//		 BlobServiceSasSignatureValues myValues = new BlobServiceSasSignatureValues(myExpiryTime, myPermission)
//		     .setStartTime(OffsetDateTime.now());
//		 
//		 client.generateUserDelegationSas(myValues, userDelegationKey);
//	}
	
}
