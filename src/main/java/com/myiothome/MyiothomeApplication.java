package com.myiothome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MyiothomeApplication {

	@PostConstruct
	public void init(){
		//Redis和elasticsearch的底层都是调用Netty，设置此消除冲突
		//see Netty4Utils.setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(MyiothomeApplication.class, args);
	}

}
