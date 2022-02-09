package com.azure.storage.storageaccountsample.sas;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SasController {

	@Autowired
	SasHelper helper;

	@Autowired
    DirectUploadHelper directUploadHelper; // (1)
	
	
    @GetMapping("/readsas")
    @ResponseBody
    @CrossOrigin
    public DirectUploadAuthInfo readSas(@RequestParam("filename") String fileName) throws IOException {
    	String sasStr = helper.getServiceSasUriForBlob();
    	System.out.println(sasStr);

    	String directUploadContainerName = "keisamplecontainer";
        return directUploadHelper.getDirectUploadInfo(directUploadContainerName, fileName, sasStr); // (7)
    }
  

    
    
}