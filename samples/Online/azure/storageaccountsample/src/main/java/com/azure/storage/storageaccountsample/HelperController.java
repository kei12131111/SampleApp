package com.azure.storage.storageaccountsample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.storage.storageaccountsample.helper.StorageAccountHelper;

@RestController
@RequestMapping("helper")
public class HelperController {

	@Autowired
	StorageAccountHelper storageAccountHelper;

	@GetMapping("/upload")
	public String upload() throws IOException {

		// "ランダムな文字 + temp.txt" のファイル名を作成
		String fileName = UUID.randomUUID().toString() + "temp.txt";

		try (InputStream inputStream = new ClassPathResource("sample2.txt").getInputStream()) {
			// ファイルアップロード
			storageAccountHelper.fileUpload(inputStream, "keisamplecontainer", "tmp", fileName);

		}
		return "success";

	}

	@GetMapping("/download")
	public String download() throws IOException {

		// "ランダムな文字 + temp.txt" のファイル名を作成
		String fileName = "sample.txt";

		try (OutputStream outputStream = new FileOutputStream("C:\\worktmp\\sample.txt")) {
			// ファイルアップロード
			storageAccountHelper.fileDownload(outputStream, "keisamplecontainer", "tmp", fileName);

		}
		return "success";

	}

	@GetMapping("/delete")
	public String delete() throws IOException {

		// "ランダムな文字 + temp.txt" のファイル名を作成
		String fileName = "3945f277-c303-43d0-84e5-0c3b1599ffb7temp.txt";

		storageAccountHelper.fileDelete("keisamplecontainer", "tmp", fileName);

		return "deletesuccess";

	}
	
	@GetMapping("/copy")
	public String copy() throws IOException {

		String fileName = "06114ba1-af4e-42e3-987b-77f1088d14eatemp.txt";

		storageAccountHelper.fileCopy("keisamplecontainer", "tmp", fileName, "keisamplecontainer2", "tmp", fileName);

		return "copysuccess";

	}
	
	@GetMapping("/search")
	public String search() throws IOException {

		storageAccountHelper.fileSearch("keisamplecontainer", "tmp", "*" + "2");

		return "searchsuccess";

	}

}