package com.azure.storage.storageaccountsample.sas;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SasController {

	@Autowired
	SasHelper helper;

    @GetMapping("/readsas")
    public String readSas() throws IOException {
    	String sasStr = helper.getServiceSasUriForBlob();
    	System.out.println(sasStr);
    	return "home";
    	
    	
    }
  

    
    
}