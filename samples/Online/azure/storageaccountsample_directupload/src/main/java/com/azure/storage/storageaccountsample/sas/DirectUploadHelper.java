package com.azure.storage.storageaccountsample.sas;

import java.security.Policy;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.stereotype.Component;




@Component
public class DirectUploadHelper implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

//    // omitted
//
//    @Value("${upload.roleName}")
//    String roleName; // (1)
//
//    @Value("${upload.roleSessionName}")
//    String roleSessionName; // (1)
//
//    private String roleArn;
//
//    private static final int STS_MIN_DURATION_MINUTES = 15;
//
//    private Credentials getTemporaryCredentials(String bucketName, String objectKey) {
//
//        String resourceArn = "arn:aws:s3:::" + bucketName + "/" + objectKey;
//
//        // (2)
//        Statement statement = new Statement(Statement.Effect.Allow)
//                .withActions(S3Actions.PutObject)
//                .withResources(new Resource(resourceArn));
//        String iamPolicy = new Policy().withStatements(statement).toJson();
//
//        int minDurationSeconds = (int) TimeUnit.MINUTES.toSeconds(STS_MIN_DURATION_MINUTES);
//
//        // (3)
//        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
//                .withRoleArn(roleArn)
//                .withDurationSeconds(minDurationSeconds)
//                .withRoleSessionName(roleSessionName)
//                .withPolicy(iamPolicy);
//
//        return AWSSecurityTokenServiceClientBuilder.defaultClient()
//                .assumeRole(assumeRoleRequest).getCredentials(); // (4)
//    }
//
//    // (5)
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        GetRoleRequest request = new GetRoleRequest();
//        request.setRoleName(roleName);
//
//        roleArn = AmazonIdentityManagementClientBuilder.defaultClient()
//                .getRole(request).getRole().getArn();
//    }

    // omitted
}
