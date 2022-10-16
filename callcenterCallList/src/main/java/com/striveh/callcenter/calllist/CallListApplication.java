package com.striveh.callcenter.calllist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.striveh.callcenter.calllist","com.striveh.callcenter.common","com.striveh.callcenter.feignclient"},
		exclude = {ErrorMvcAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.striveh.callcenter.feignclient"})
public class CallListApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallListApplication.class, args);
	}
}
