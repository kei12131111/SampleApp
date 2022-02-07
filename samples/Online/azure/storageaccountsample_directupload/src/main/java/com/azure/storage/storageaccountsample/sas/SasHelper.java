package com.azure.storage.storageaccountsample.sas;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;

@Component
public class SasHelper {

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
	
	
	public String getServiceSasUriForBlob() {
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(accountKey).buildClient();
		 
	    // SignedPermission (sp): AccountSasPermission.parse("rwacd") でも可
	    var permissions = new AccountSasPermission()
	            .setReadPermission(true)
	            .setWritePermission(true)
	            .setAddPermission(true)
	            .setCreatePermission(true)
	            .setDeletePermission(true);
	 
	    // SignedResourceTypes (srt): AccountSasResourceType.parse("oc") でも可
	    var resourceTypes = new AccountSasResourceType()
	            .setContainer(true)
	            .setObject(true);
	 
	    //  SignedServices (ss): AccountSasService.parse("b") でも可
	    var services = new AccountSasService().setBlobAccess(true);
	 
	    // SignedExpiry (se): SASトークンの有効期限は5分で指定
	    var expiryTime = OffsetDateTime.now().plus(Duration.ofMinutes(5));
	 
	    // SASトークンを作成
	    var sasValues = new AccountSasSignatureValues(expiryTime, permissions, services, resourceTypes);
	    return blobServiceClient.generateAccountSas(sasValues);
	}
	

	
}
