package com.azure.storage.storageaccountsample.sas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("upload")
public class DirectUploadController {

//    @Value("${upload.bucketName}")
//    String directUploadBucketName;
//
//    @Autowired
//    DirectUploadHelper directUploadHelper; // (1)
//
//    @GetMapping // (2)
//    public String upload() {
//        return "upload/index";
//    }
//
//    @GetMapping(params = "info") // (3)
//    @ResponseBody // (4)
//    public DirectUploadAuthInfo getInfoForDirectUpload(
//            @RequestParam("filename") String fileName, // (5)
//            @AuthenticationPrincipal SampleUserDetails userDetails) { // (6)
//
//        return directUploadHelper.getDirectUploadInfo(directUploadBucketName,
//                fileName, userDetails); // (7)
//    }
}
