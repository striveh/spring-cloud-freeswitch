/** */
package com.striveh.callcenter.common.base.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 *           <bean id="conversionService"
 *           class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
 *           <property name="converters">
 *           <set>
 *           <ref bean="dateConvert" />
 *           <ref bean="sqlDateConvert" />
 *           <ref bean="timestampConvert" />
 *           </set>
 *           </property>
 *           </bean>
 *           </pre>
 */
public class DateConvert implements Converter<String, Date> {
	/**
	 * @方法重写
	 */
	@Override
	public Date convert(String source) {
		if (!StringUtils.isEmpty(source)) {
			try {
				String format = "yyyy-MM-dd HH:mm:ss.SSS".substring(0, source.length());
				return new SimpleDateFormat(format).parse(source);
			} catch (ParseException e) {
				return new Date(Long.valueOf(source));
			}
		}
		return null;
	}
}
