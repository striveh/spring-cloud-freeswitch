package com.striveh.callcenter.common.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonHttpMessageConverterConfig {

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters(){
		//创建FastJson信息转换对象
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();

		//创建Fastjosn对象并设定序列化规则
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		fastJsonConfig.setCharset(Charset.forName("UTF-8"));
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

		//规则赋予转换对象
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

		return new HttpMessageConverters(fastJsonHttpMessageConverter);

	}

}
