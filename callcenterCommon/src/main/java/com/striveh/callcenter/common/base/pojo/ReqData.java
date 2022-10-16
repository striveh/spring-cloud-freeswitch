package com.striveh.callcenter.common.base.pojo;

import java.io.Serializable;
import java.util.Map;

public class ReqData<T> implements Serializable {
	/**  */
	private static final long serialVersionUID = 7733225164742044866L;
	/** request请求头 */
	public Map<String, Object> reqHeader;
	/** request请求数据 */
	private T reqData;

	/**
	 * @取得 request请求头
	 */
	public Map<String, Object> getReqHeader() {
		return reqHeader;
	}

	/**
	 * @设置 request请求头
	 */
	public void setReqHeader(Map<String, Object> reqHeader) {
		this.reqHeader = reqHeader;
	}

	/**
	 * @取得 request请求数据
	 */
	public T getReqData() {
		return reqData;
	}

	/**
	 * @设置 request请求数据
	 */
	public void setReqData(T reqData) {
		this.reqData = reqData;
	}

}