package com.azure.storage.storageaccountsample.sas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.azure.storage.blob.sas.BlobSasPermission;

@Controller
@RequestMapping("upload")
public class DirectUploadController {
	
    @Value("${cloud.azurestorageaccount.upload.expiration:30}")
    private int seconds;

	@Autowired
    StorageAccount2Helper storageAccount2Helper;

    @CrossOrigin
    @GetMapping(params = "info")
    @ResponseBody
    public DirectOperationAuthInfo getInfoForDirectUpload() {
    	
		 BlobSasPermission permission = new BlobSasPermission()
		            .setWritePermission(true)
		            .setAddPermission(true)
		            .setCreatePermission(true);

        return storageAccount2Helper.getDirectOperationAuthInfo(seconds, permission);
    }
}
