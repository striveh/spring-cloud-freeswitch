/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【sip_presence 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class SipPresencePojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String sipUser;
	/**  */
	private String sipHost;
	/**  */
	private String status;
	/**  */
	private String rpid;
	/**  */
	private Long expires;
	/**  */
	private String userAgent;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private String networkIp;
	/**  */
	private String networkPort;
	/**  */
	private String openClosed;

	
	/** @取得   */
	public String getSipUser(){
		return sipUser;
	}
	
	/** @设置   */
	public void setSipUser(String sipUser){
		this.sipUser = sipUser;
	}
	
	/** @取得   */
	public String getSipHost(){
		return sipHost;
	}
	
	/** @设置   */
	public void setSipHost(String sipHost){
		this.sipHost = sipHost;
	}
	
	/** @取得   */
	public String getStatus(){
		return status;
	}
	
	/** @设置   */
	public void setStatus(String status){
		this.status = status;
	}
	
	/** @取得   */
	public String getRpid(){
		return rpid;
	}
	
	/** @设置   */
	public void setRpid(String rpid){
		this.rpid = rpid;
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
	public String getUserAgent(){
		return userAgent;
	}
	
	/** @设置   */
	public void setUserAgent(String userAgent){
		this.userAgent = userAgent;
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
	public String getNetworkIp(){
		return networkIp;
	}
	
	/** @设置   */
	public void setNetworkIp(String networkIp){
		this.networkIp = networkIp;
	}
	
	/** @取得   */
	public String getNetworkPort(){
		return networkPort;
	}
	
	/** @设置   */
	public void setNetworkPort(String networkPort){
		this.networkPort = networkPort;
	}
	
	/** @取得   */
	public String getOpenClosed(){
		return openClosed;
	}
	
	/** @设置   */
	public void setOpenClosed(String openClosed){
		this.openClosed = openClosed;
	}

}
