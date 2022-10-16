/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【calls 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:01
 * @说明：<pre></pre>
 */
public class CallsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String callUuid;
	/**  */
	private String callCreated;
	/**  */
	private Integer callCreatedEpoch;
	/**  */
	private String callerUuid;
	/**  */
	private String calleeUuid;
	/**  */
	private String hostname;

	
	/** @取得   */
	public String getCallUuid(){
		return callUuid;
	}
	
	/** @设置   */
	public void setCallUuid(String callUuid){
		this.callUuid = callUuid;
	}
	
	/** @取得   */
	public String getCallCreated(){
		return callCreated;
	}
	
	/** @设置   */
	public void setCallCreated(String callCreated){
		this.callCreated = callCreated;
	}
	
	/** @取得   */
	public Integer getCallCreatedEpoch(){
		return callCreatedEpoch;
	}
	
	/** @设置   */
	public void setCallCreatedEpoch(Integer callCreatedEpoch){
		this.callCreatedEpoch = callCreatedEpoch;
	}
	
	/** @取得   */
	public String getCallerUuid(){
		return callerUuid;
	}
	
	/** @设置   */
	public void setCallerUuid(String callerUuid){
		this.callerUuid = callerUuid;
	}
	
	/** @取得   */
	public String getCalleeUuid(){
		return calleeUuid;
	}
	
	/** @设置   */
	public void setCalleeUuid(String calleeUuid){
		this.calleeUuid = calleeUuid;
	}
	
	/** @取得   */
	public String getHostname(){
		return hostname;
	}
	
	/** @设置   */
	public void setHostname(String hostname){
		this.hostname = hostname;
	}

}
