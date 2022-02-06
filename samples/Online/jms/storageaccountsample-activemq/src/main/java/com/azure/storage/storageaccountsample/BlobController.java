package com.azure.storage.storageaccountsample;


import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.spring.autoconfigure.storage.resource.BlobStorageResource;

@RestController
@RequestMapping("blob")
public class BlobController {

	
	@Autowired
	private ResourceLoader resourceLoader;
//    @Value("azure-blob://keisamplecontainer/sample.txt")
//    private Resource blobFile;

    @GetMapping("/readBlobFile")
    public String readBlobFile() throws IOException {
    	
    	Resource resource = resourceLoader.getResource("azure-blob://keisamplecontainer/sample.txt");
    	
    	
        return StreamUtils.copyToString(
        		resource.getInputStream(),
                Charset.defaultCharset());
    }
  

    
    
}