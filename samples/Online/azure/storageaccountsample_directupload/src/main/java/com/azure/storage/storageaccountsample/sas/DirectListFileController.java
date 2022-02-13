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
@RequestMapping("list")
public class DirectListFileController {
	
    @Value("${cloud.azurestorageaccount.list.expiration:30}")
    private int seconds;

	@Autowired
    StorageAccount2Helper storageAccount2Helper;

    @GetMapping
    public String upload() {
        return "storageAccountSample";
    }
	
    @CrossOrigin
    @GetMapping(params = "info")
    @ResponseBody
    public DirectOperationAuthInfo getInfoForDirectUpload() {
    	
		 BlobSasPermission permission = new BlobSasPermission()
		            .setListPermission(true);

        return storageAccount2Helper.getDirectOperationAuthInfo(seconds, permission);
    }
}
