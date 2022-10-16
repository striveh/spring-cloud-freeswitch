/** */
package com.striveh.callcenter.common.util;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.http.converter.HttpMessageConverter;


public class JsonTool {
	/** 日志对象 */
	protected static Logger logger = LogManager.getLogger(JsonTool.class);

	/**
	 * 转换Json字符串：无格式、日期(数字)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getJsonString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * 转换Json字符串：无格式、日期(yyyy-MM-dd HH:mm:ss.SSS)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getJsonString(Object obj, String dateFmt) {
		if (dateFmt == null) {
			dateFmt = "yyyy-MM-dd HH:mm:ss.SSS";
		}
		return JSON.toJSONStringWithDateFormat(obj, dateFmt);
	}

	/**
	 * 转换Json字符串：日期(数字)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getFormatJsonString(Object obj) {
		return getJsonString(obj, false, null, true);
	}

	/**
	 * 转换Json字符串：日期(yyyy-MM-dd HH:mm:ss.SSS)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getFormatString(Object obj, String dateFmt) {
		if (dateFmt == null) {
			dateFmt = "yyyy-MM-dd HH:mm:ss.SSS";
		}
		return JSON.toJSONStringWithDateFormat(obj, dateFmt, SerializerFeature.PrettyFormat);
	}

	/***
	 * 自定义转换Json字符串：无格式、日期(数字)、包含空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getJsonString(Object obj, boolean isAllField, String dateFmt, boolean isFormatText) {
		SerializeWriter out = new SerializeWriter();
		try {
			// QuoteFieldNames———-输出key时是否使用双引号,默认为true
			// WriteMapNullValue——–是否输出值为null的字段,默认为false
			// WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
			// WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
			// WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
			// WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
			// DisableCircularReferenceDetect禁用循环引用{"s1":{"age":16},"s2":{"$ref":"$.s1"}}
			JSONSerializer serializer = new JSONSerializer(out);
			//serializer.config(SerializerFeature.DisableCircularReferenceDetect, true);
			if (isFormatText) {// 格式化json文本
				serializer.config(SerializerFeature.PrettyFormat, true);
				serializer.config(SerializerFeature.SortField, true);
			}
			if (dateFmt != null) {// 格式化日期
				serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
				serializer.setDateFormat(dateFmt);
			}
			if (isAllField) {// 所有字段
				serializer.config(SerializerFeature.WriteMapNullValue, true);
			}
			serializer.write(obj);
			return out.toString();
		} finally {
			out.close();
		}

	}

	/**
	 * 转换Json字符串：无格式、日期(数字)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj) {
		return JSONObject.toJSONString(obj);
	}

	/**
	 * 转换Json字符串：无格式、日期(yyyy-MM-dd HH:mm:ss.SSS)、非空对象
	 *
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj, String dateFmt) {
		if (dateFmt == null) {
			dateFmt = "yyyy-MM-dd HH:mm:ss.SSS";
		}
		return getString(obj, dateFmt, false, false);
	}

	/**
	 * 自定义转换Json字符串：无格式、日期(数字)、包含空对象
	 *
	 * @param obj
	 * @param dateFmt 日期格式
	 * @param isAllField 是否所有字段
	 * @param isFormatText 是否美化输出
	 * @return
	 */
	public static String getString(Object obj, String dateFmt, boolean isAllField, boolean isFormatText) {
		SerializeWriter out = new SerializeWriter();
		try {
			// QuoteFieldNames———-输出key时是否使用双引号,默认为true
			// WriteMapNullValue——–是否输出值为null的字段,默认为false
			// WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
			// WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
			// WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
			// WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
			// DisableCircularReferenceDetect禁用循环引用{"s1":{"age":16},"s2":{"$ref":"$.s1"}}
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.config(SerializerFeature.DisableCircularReferenceDetect, true);
			if (isFormatText) {// 格式化json文本
				serializer.config(SerializerFeature.PrettyFormat, true);
				serializer.config(SerializerFeature.SortField, true);
			}
			if (dateFmt != null) {// 格式化日期
				serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
				serializer.setDateFormat(dateFmt);
			}
			if (isAllField) {// 所有字段
				serializer.config(SerializerFeature.WriteMapNullValue, true);
			}
			serializer.write(obj);
			return out.toString();
		} finally {
			out.close();
		}
	}

	/***
	 * 将json字符串转换成对象
	 *
	 * @param str
	 * @param cls
	 * @return
	 */
	public static <T> T getObj(String str, Class<T> cls) {
		if (str == null) {
			return null;
		}
		return JSON.parseObject(str, cls);
	}

	/**
	 * 将json字符串转成List
	 *
	 * @param str
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getList(String str, Class<T> cls) {
		if (str == null) {
			return null;
		}
		return JSON.parseArray(str, cls);
	}

	/**
	 * 替换spring原有的json工具,需继承WebMvcConfigurerAdapter类，重写configureMessageConverters方法
	 *
	 * @param converters
	 */
	public static void addConverter(List<HttpMessageConverter<?>> converters) {
		// PrettyFormat———-格式化json文本
		// QuoteFieldNames———-输出key时是否使用双引号,默认为true
		// WriteMapNullValue———-是否输出值为null的字段,默认为false
		// WriteNullNumberAsZero———-数值字段如果为null,输出为0,而非null
		// WriteNullListAsEmpty———-List字段如果为null,输出为[],而非null
		// WriteNullStringAsEmpty———-字符类型字段如果为null,输出为”“,而非null
		// WriteNullBooleanAsFalse———-Boolean字段如果为null,输出为false,而非null
		// DisableCircularReferenceDetect———-禁用循环引用{"s1":{"age":16},"s2":{"$ref":"$.s1"}}
		// WriteDateUseDateFormat———-格式化日期
		FastJsonConfig fjConfig = new FastJsonConfig();
		// fjConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.WriteMapNullValue);//
//		fjConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		converter.setFastJsonConfig(fjConfig);
		converters.add(0,converter);
	}
}
