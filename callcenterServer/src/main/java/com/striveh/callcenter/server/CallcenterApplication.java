package com.striveh.callcenter.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.striveh.callcenter.server","com.striveh.callcenter.common","com.striveh.callcenter.feignclient"},
		exclude = {ErrorMvcAutoConfiguration.class })
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.striveh.callcenter.feignclient"})
@EnableAsync
@EnableScheduling
public class CallcenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallcenterApplication.class, args);
	}

}
