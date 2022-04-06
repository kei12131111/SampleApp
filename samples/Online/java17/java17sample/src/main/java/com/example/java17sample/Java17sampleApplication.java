package com.example.java17sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Java17sampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(Java17sampleApplication.class, args);
		RecordSample recordSample = new RecordSample("hoge");
		
		String name = recordSample.name();
		System.out.println(name);
		
		Object obj = new String("aaa");
		if (obj instanceof String str) {
		    System.out.println("objはStringのインスタンス");
			System.out.println(str);
		}
	}

}
