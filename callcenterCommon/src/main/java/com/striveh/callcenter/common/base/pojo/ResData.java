package com.striveh.callcenter.common.base.pojo;

import java.io.Serializable;
import java.util.Map;

public class ResData<T>  implements Serializable {
	/**  */
	private static final long serialVersionUID = -7971552241067994527L;
	/** response头 */
	private Map<String, Object> resHeader;
	/** response结果 */
	private ResResult resResult;
	/** response数据 */
	private T resData;

	/**
	 * @取得 response头
	 */
	public Map<String, Object> getResHeader() {
		return resHeader;
	}

	/**
	 * @设置 response头
	 */
	public void setResHeader(Map<String, Object> resHeader) {
		this.resHeader = resHeader;
	}

	/**
	 * @取得 response结果
	 */
	public ResResult getResResult() {
		return resResult;
	}

	/**
	 * @设置 response结果
	 */
	public void setResResult(ResResult resResult) {
		this.resResult = resResult;
	}

	/**
	 * @取得 response数据
	 */
	public T getResData() {
		return resData;
	}

	/**
	 * @设置 response数据
	 */
	public void setResData(T resData) {
		this.resData = resData;
	}

}