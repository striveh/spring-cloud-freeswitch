/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【sip_shared_appearance_subscriptions 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class SipSharedAppearanceSubscriptionsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String subscriber;
	/**  */
	private String callId;
	/**  */
	private String aor;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private String contactStr;
	/**  */
	private String networkIp;

	
	/** @取得   */
	public String getSubscriber(){
		return subscriber;
	}
	
	/** @设置   */
	public void setSubscriber(String subscriber){
		this.subscriber = subscriber;
	}
	
	/** @取得   */
	public String getCallId(){
		return callId;
	}
	
	/** @设置   */
	public void setCallId(String callId){
		this.callId = callId;
	}
	
	/** @取得   */
	public String getAor(){
		return aor;
	}
	
	/** @设置   */
	public void setAor(String aor){
		this.aor = aor;
	}
	
	/** @取得   */
	public String getProfileName(){
		return profileName;
	}
	
	/** @设置   */
	public void setProfileName(String profileName){
		this.profileName = profileName;
	}
	
	/** @取得   */
	public String getHostname(){
		return hostname;
	}
	
	/** @设置   */
	public void setHostname(String hostname){
		this.hostname = hostname;
	}
	
	/** @取得   */
	public String getContactStr(){
		return contactStr;
	}
	
	/** @设置   */
	public void setContactStr(String contactStr){
		this.contactStr = contactStr;
	}
	
	/** @取得   */
	public String getNetworkIp(){
		return networkIp;
	}
	
	/** @设置   */
	public void setNetworkIp(String networkIp){
		this.networkIp = networkIp;
	}

}
