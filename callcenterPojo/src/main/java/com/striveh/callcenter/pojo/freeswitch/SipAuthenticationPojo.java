/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【sip_authentication 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class SipAuthenticationPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String nonce;
	/**  */
	private Long expires;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private Integer lastNc;

	
	/** @取得   */
	public String getNonce(){
		return nonce;
	}
	
	/** @设置   */
	public void setNonce(String nonce){
		this.nonce = nonce;
	}
	
	/** @取得   */
	public Long getExpires(){
		return expires;
	}
	
	/** @设置   */
	public void setExpires(Long expires){
		this.expires = expires;
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
	public Integer getLastNc(){
		return lastNc;
	}
	
	/** @设置   */
	public void setLastNc(Integer lastNc){
		this.lastNc = lastNc;
	}

}
