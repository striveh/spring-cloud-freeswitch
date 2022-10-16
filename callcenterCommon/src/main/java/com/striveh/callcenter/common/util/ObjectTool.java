/** */
package com.striveh.callcenter.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public class ObjectTool {
	/** 日志 */
	protected static Logger logger = LogManager.getLogger(ObjectTool.class);

	/**
	 * 用反射方法，将map中的值设置给对象对应属性
	 * 
	 * @param obj
	 * @param valMap
	 * @return
	 * @throws Exception
	 */
	public static <T> T setValue(T obj, Map<String, Object> valMap) throws Exception {
		return setValue(obj, valMap, true);
	}

	/**
	 * 用反射方法，将map中的值设置给对象对应属性
	 * 
	 * @param obj
	 * @param valMap
	 * @return
	 * @throws Exception
	 */
	public static <T> T setValue(T obj, Map<String, Object> valMap, boolean skipEmpty) throws Exception {
		Field field = null;
		Class<?> clazz = obj.getClass();
		for (Entry<String, Object> entry : valMap.entrySet()) {
			if (ObjectUtils.isEmpty(obj) && skipEmpty) {
				continue;
			}
			field = ReflectionUtils.findField(clazz, entry.getKey());
			field.setAccessible(true);
			if (field.getType().equals(int.class)) {
				field.set(obj, Integer.valueOf(entry.getValue().toString()).intValue());
			} else if (field.getType().equals(Integer.class)) {
				field.set(obj, Integer.valueOf(entry.getValue().toString()));
			} else if (field.getType().equals(long.class)) {
				field.set(obj, Long.valueOf(entry.getValue().toString()).longValue());
			} else if (field.getType().equals(Long.class)) {
				field.set(obj, Long.valueOf(entry.getValue().toString()));
			} else if (field.getType().equals(float.class)) {
				field.set(obj, Float.valueOf(entry.getValue().toString()).floatValue());
			} else if (field.getType().equals(Float.class)) {
				field.set(obj, Float.valueOf(entry.getValue().toString()));
			} else if (field.getType().equals(double.class)) {
				field.set(obj, Double.valueOf(entry.getValue().toString()).doubleValue());
			} else if (field.getType().equals(Double.class)) {
				field.set(obj, Double.valueOf(entry.getValue().toString()));
			} else {
				field.set(obj, entry.getValue());
			}
		}
		return obj;
	}

	/**
	 * 判断string是否为空或空字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> boolean isEmpty(T obj) {
		if (obj instanceof String) {
			return obj == null || obj.toString().trim().length() < 1;
		} else if (obj instanceof Collection) {
			return obj == null || ((Collection<?>) obj).size() == 0;
		} else {
			return obj == null;
		}
	}

	/**
	 * 判断string是否不为空或空字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> boolean isNotEmpty(T obj) {
		return !isEmpty(obj);
	}

	/**
	 * 如果obj为空，返回trueVal,否则返回自己
	 * 
	 * @param obj
	 * @param trueVal
	 * @return
	 */
	public static <T> T ifObjEmpty(T obj, T trueVal) {
		return isEmpty(obj) ? trueVal : obj;
	}



	/**
	 *
	 * @param obj
	 * @return true if not null
	 */
	public static boolean checkFieldValueNull(Object obj,String... str) {
		try {
			for (Field f : obj.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(obj) == null || f.get(obj) == "") { //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
					//这里可以给空字段初始化，及其他操作
					if (str != null) {
						if ("completeData".equals(str) || "cardId".equals(str)) {
							continue;
						}
					}
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

 	/** 将对象装换为map
		* @param bean
		* @return
	 * */
	public static <T> Map<String, Object> beanToMap(T bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				map.put(key+"", beanMap.get(key));
			}
		}
		return map;
	}

	/**
	 * 将map装换为javabean对象
	 * @param map
	 * @param bean
	 * @return
	 */
	public static <T> T mapToBean(Map<String, Object> map,T bean) {
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}

	/**
	 * 将List<T>转换为List<Map<String, Object>>
	 * @param objList
	 * @return
	 */
	public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
		List<Map<String, Object>> list = Lists.newArrayList();
		if (objList != null && objList.size() > 0) {
			Map<String, Object> map = null;
			T bean = null;
			for (int i = 0,size = objList.size(); i < size; i++) {
				bean = objList.get(i);
				map = beanToMap(bean);
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 将List<Map<String,Object>>转换为List<T>
	 * @param maps
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {
		List<T> list = Lists.newArrayList();
		if (maps != null && maps.size() > 0) {
			Map<String, Object> map = null;
			T bean = null;
			for (int i = 0,size = maps.size(); i < size; i++) {
				map = maps.get(i);
				bean = clazz.newInstance();
				mapToBean(map, bean);
				list.add(bean);
			}
		}
		return list;
	}

	public static void main(String[] args) throws Exception {

	}
}
