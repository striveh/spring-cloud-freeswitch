package com.striveh.callcenter.freeswitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication( scanBasePackages = {"com.striveh.callcenter.freeswitch","com.striveh.callcenter.common","com.striveh.callcenter.feignclient"},
		exclude = {ErrorMvcAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.striveh.callcenter.feignclient"})
public class FreeswitchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeswitchApplication.class, args);
	}
}
