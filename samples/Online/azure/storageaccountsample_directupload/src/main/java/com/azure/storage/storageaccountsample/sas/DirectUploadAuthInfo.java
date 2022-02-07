package com.azure.storage.storageaccountsample.sas;

public class DirectUploadAuthInfo {

    private String targetUrl;

    private String acl;

    private String policy;

    private String securityToken;

    private String objectKey;

    private String date;

    private String algorithm;

    private String credential;

    private String signature;

    private String rawFileName;

    private String fileSizeLimit;

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getRawFileName() {
		return rawFileName;
	}

	public void setRawFileName(String rawFileName) {
		this.rawFileName = rawFileName;
	}

	public String getFileSizeLimit() {
		return fileSizeLimit;
	}

	public void setFileSizeLimit(String fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}
	
}
