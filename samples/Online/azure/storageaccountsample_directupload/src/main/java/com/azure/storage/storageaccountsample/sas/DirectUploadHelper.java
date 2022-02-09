package com.azure.storage.storageaccountsample.sas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;




@Component
public class DirectUploadHelper implements InitializingBean {

	/**
	 * Azureストレージアカウントの名前。
	 */
	@Value("${azure.storage.account-name}")
	String accountName;
	
    public DirectUploadAuthInfo getDirectUploadInfo(String containerName,
            String fileName, String sas) {

//        String objectKey = createObjectKey(userDetails); // (3)

//        Credentials credentials = getTemporaryCredentials(bucketName, objectKey); // (4)
//
//        String regionName = regionProvider.getRegion().getName(); // (5)

//        String serviceName = "s3";

//        DateTime nowUTC = new DateTime(DateTimeZone.UTC); // (6)
    	
		String targetUrl = String.format(Locale.ROOT, "https://%s.blob.core.windows.net/", accountName);
		targetUrl = targetUrl + containerName + "/" + fileName + "?" + sas;
		
        String xmsVersion = "2020-10-02";
        
    	Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String xmsDate = df.format(now);
        
        String xmsBlobType = "BlockBlob";

//        String acl = "private";

//        String credentialString = credentials.getAccessKeyId() + "/" + date + "/"
//                    + regionName + "/" + serviceName + "/" + "aws4_request";  // (8)

//        String securityToken = credentials.getSessionToken();
//
//        String s3endpoint = "s3-" + regionName + ".amazonaws.com";
//必要かも
//		String targetUrl = "https://" + bucketName + "." + s3endpoint + "/";

//        String algorithm = "AWS4-HMAC-SHA256"; // (9)
//
//        String iso8601dateTime = nowUTC.toString("yyyyMMdd'T'HHmmss'Z'"); // (10)
		
        DirectUploadAuthInfo res = new DirectUploadAuthInfo();
        res.setTargetUrl(targetUrl);
        res.setXmsVersion(xmsVersion);
        res.setXmsDate(xmsDate);
        res.setXmsBlobType(xmsBlobType);
        
		
		return res;
    }
	
	
	

//    // (3)
//    private String createObjectKey(SampleUserDetails userDetails) {
//        String userId = userDetails.getUsername();
//        return userId + "/" + UUID.randomUUID();
//    }

//    private byte[] getSignatureKey(String key, String dateStamp, String region,
//            String serviceName) {
//        byte[] kSecret  = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
//        byte[] kDate    = calculateHmacSHA256(dateStamp, kSecret);
//        byte[] kRegion  = calculateHmacSHA256(region, kDate);
//        byte[] kService = calculateHmacSHA256(serviceName, kRegion);
//        byte[] kSigning = calculateHmacSHA256("aws4_request", kService);
//        return kSigning;
//    }
//
//    private byte[] calculateHmacSHA256(String stringToSign, byte[] signingKey) {
//        String algorithm = "HmacSHA256";
//        Mac mac = null;
//        try {
//            mac = Mac.getInstance(algorithm);
//        } catch (NoSuchAlgorithmException e) {
//            throw new SystemException("e.xx.fw.9001", "invalid algorithm.", e);
//        }
//        try {
//            mac.init(new SecretKeySpec(signingKey, algorithm));
//        } catch (InvalidKeyException e) {
//            throw new SystemException("e.xx.fw.9001", "invalid encoding key.", e);
//        }
//
//        return mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
//    }

    // (11)
//    private class PostPolicy {
//
//        private String expiration;
//
//        private String[][] conditions;
//
//        // accessor is omitted
//    }




	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
