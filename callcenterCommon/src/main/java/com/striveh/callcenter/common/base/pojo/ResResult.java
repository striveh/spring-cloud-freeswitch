package com.striveh.callcenter.common.base.pojo;

import java.io.Serializable;

public class ResResult implements Serializable {
	/**  */
	private static final long serialVersionUID = 7206233681863325247L;
	/** 操作成功状态码：0成功，1失败 */
	private Integer statusCode;
	/** 错误状态码 */
	private Integer errCode;
	/** 错误状态码 */
	private String errMsg;

	/**
	 * @取得 操作成功状态码：0成功，1失败
	 */
	public Integer getStatusCode() {
		return statusCode;
	}

	/**
	 * @设置 操作成功状态码：0成功，1失败
	 */
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @取得 错误状态码
	 */
	public Integer getErrCode() {
		return errCode;
	}

	/**
	 * @设置 错误状态码
	 */
	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}

	/**
	 * @取得 错误状态码
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @设置 错误状态码
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}