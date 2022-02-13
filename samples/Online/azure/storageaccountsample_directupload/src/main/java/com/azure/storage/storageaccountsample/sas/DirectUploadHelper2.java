package com.azure.storage.storageaccountsample.sas;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;

/**
 * ストレージアカウントへのダイレクトアップロード用helperクラス。
 */
@Component
public class DirectUploadHelper2 implements InitializingBean {

	/**
	 * Azureストレージアカウントの名前。
	 */
	@Value("${azure.storage.account-name}")
	String accountName;

	/**
	 * コンテナの名前。
	 */
	@Value("${azure.storage.container-name}")
	String containerName;
	
	/**
	 * アプリケーション(クライアント)ID
	 */
	@Value("${azure.application.client-id}")
	String clientId;
	
	/**
	 * クライアントシークレット
	 */
	@Value("${azure.application.client-secret}")
	String clientSecret;
	
	/**
	 * テナントID
	 */
	@Value("${azure.application.tenant-id}")
	String tenantId;
	
	/**
	 * BLOBの種類 (ブロックBLOB、ページBLOB、追加BLOB)
	 */
	final String XMS_BLOB_TYPE = "BlockBlob";
	
    /**
     * ダイレクトアップロード用の情報を取得する。
     * @param fileName ファイル名
     * @return DirectUploadAuthInfo ダイレクトアップロード用の情報
     */
	public DirectOperationAuthInfo getDirectOperationAuthInfo(String fileName) {
		
		Map<String, String> userDelegationSasInfo = getUserDelegationSas(fileName);
		

		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net/", accountName);
		String targetUrl = endpoint + "?" + userDelegationSasInfo.get("userDelegationSas");
		

		String xmsVersion = userDelegationSasInfo.get("signedVersion");

		// 本日の日付(UTC日時)をyyyy-MM-dd形式に変換
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String xmsDate = df.format(new Date());
		
		DirectOperationAuthInfo res = new DirectOperationAuthInfo();
		res.setTargetUrl(targetUrl);

		return res;
	}

    /**
     * ユーザ委任SASを取得する。
     * @param fileName ファイル名
     * @return String ユーザ委任SAS情報
     */	
	private Map<String, String> getUserDelegationSas(String fileName) {

		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
				.clientId(clientId).clientSecret(clientSecret)
				.tenantId(tenantId).build();

		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net/", accountName);
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.credential(clientSecretCredential).endpoint(endpoint)
				.buildClient();
		
		
		BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
//		BlockBlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
//				.getBlobClient(fileName).getBlockBlobClient();

		OffsetDateTime  keyStart = OffsetDateTime.now();
		OffsetDateTime  keyExpiry = OffsetDateTime.now().plus(Duration.ofMinutes(50));
		// ユーザ委任キーの有効開始時間と有効期限を設定し、キーを取得
		UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);

		OffsetDateTime expiryTime = OffsetDateTime.now().plus(Duration.ofMinutes(50));
		 BlobSasPermission permission = new BlobSasPermission()
				 .setReadPermission(true)
		            .setWritePermission(true)
		            .setAddPermission(true)
		            .setCreatePermission(true)
		            .setListPermission(true)
		            .setDeletePermission(true);
		 // ユーザ委任SASの有効期限と権限を設定
		 BlobServiceSasSignatureValues signatureValues = new BlobServiceSasSignatureValues(expiryTime, permission)
		     .setStartTime(OffsetDateTime.now());
		 
//		 String userDelegationSas = blobClient.generateUserDelegationSas(signatureValues, userDelegationKey);
		 String userDelegationSas = blobContainerClient.generateUserDelegationSas(signatureValues, userDelegationKey);
		 
		 Map<String, String> userDelegationSasInfo = new HashMap<>();
		 userDelegationSasInfo.put("signedVersion", userDelegationKey.getSignedVersion());
		 userDelegationSasInfo.put("userDelegationSas", userDelegationSas);
		 	 
		 return userDelegationSasInfo;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
