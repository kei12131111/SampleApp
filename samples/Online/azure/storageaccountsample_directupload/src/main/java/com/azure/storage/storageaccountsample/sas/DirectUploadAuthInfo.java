package com.azure.storage.storageaccountsample.sas;

public class DirectUploadAuthInfo {

    private String targetUrl;

    private String xmsVersion;

    private String xmsDate;

    private String xmsBlobType;

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getXmsVersion() {
		return xmsVersion;
	}

	public void setXmsVersion(String xmsVersion) {
		this.xmsVersion = xmsVersion;
	}

	public String getXmsDate() {
		return xmsDate;
	}

	public void setXmsDate(String xmsDate) {
		this.xmsDate = xmsDate;
	}

	public String getXmsBlobType() {
		return xmsBlobType;
	}

	public void setXmsBlobType(String xmsBlobType) {
		this.xmsBlobType = xmsBlobType;
	}

    
	
}
