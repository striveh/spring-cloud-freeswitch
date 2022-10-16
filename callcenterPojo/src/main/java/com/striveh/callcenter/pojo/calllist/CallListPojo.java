/** */
package com.striveh.callcenter.pojo.calllist;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【callList 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 12:05:00
 * @说明：<pre></pre>
 */
public class CallListPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String phone;
	private Long callListId;
	private String result;

	
	/** @取得   */
	public String getPhone(){
		return phone;
	}
	
	/** @设置   */
	public void setPhone(String phone){
		this.phone = phone;
	}

	public Long getCallListId() {
		return callListId;
	}

	public void setCallListId(Long callListId) {
		this.callListId = callListId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
