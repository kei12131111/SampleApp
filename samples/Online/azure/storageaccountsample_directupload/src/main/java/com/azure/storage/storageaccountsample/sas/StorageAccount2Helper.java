package com.azure.storage.storageaccountsample.sas;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Locale;

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

/**
 * ストレージアカウントへのダイレクト操作用helperクラス。
 */
@Component
public class StorageAccount2Helper implements InitializingBean {

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
     * ダイレクト操作用の情報を取得する。
     * @return DirectOperationAuthInfo ダイレクト操作用の情報
     */
	public DirectOperationAuthInfo getDirectOperationAuthInfo(int expirationSeconds, BlobSasPermission permission) {
		
		String userDelegationSas = getUserDelegationSas(expirationSeconds, permission);

		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net/", accountName);
		String targetUrl = endpoint + "?" + userDelegationSas;
		
		DirectOperationAuthInfo res = new DirectOperationAuthInfo();
		res.setTargetUrl(targetUrl);
		res.setContainerName(containerName);

		return res;
	}

    /**
     * ユーザ委任SASを取得する。
     * @return String ユーザ委任SAS
     */	
	private String getUserDelegationSas(int expirationSeconds, BlobSasPermission permission) {

		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
				.clientId(clientId).clientSecret(clientSecret)
				.tenantId(tenantId).build();

		String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net/", accountName);
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.credential(clientSecretCredential).endpoint(endpoint)
				.buildClient();
		
		BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

		OffsetDateTime  keyStart = OffsetDateTime.now();
		OffsetDateTime  keyExpiry = OffsetDateTime.now().plus(Duration.ofSeconds(expirationSeconds));
		// ユーザ委任キーの有効開始時間と有効期限を設定し、キーを取得
		UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);

		OffsetDateTime expiryTime = OffsetDateTime.now().plus(Duration.ofSeconds(expirationSeconds));

		 // ユーザ委任SASの有効期限と権限を設定
		 BlobServiceSasSignatureValues signatureValues = new BlobServiceSasSignatureValues(expiryTime, permission)
		     .setStartTime(OffsetDateTime.now());
		 	 
		 return blobContainerClient.generateUserDelegationSas(signatureValues, userDelegationKey);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
