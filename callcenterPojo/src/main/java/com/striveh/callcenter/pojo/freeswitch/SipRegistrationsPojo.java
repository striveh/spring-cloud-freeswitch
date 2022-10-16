/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【sip_registrations 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class SipRegistrationsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String callId;
	/**  */
	private String sipUser;
	/**  */
	private String sipHost;
	/**  */
	private String presenceHosts;
	/**  */
	private String contact;
	/**  */
	private String status;
	/**  */
	private String pingStatus;
	/**  */
	private Integer pingCount;
	/**  */
	private Long pingTime;
	/**  */
	private Integer forcePing;
	/**  */
	private String rpid;
	/**  */
	private Long expires;
	/**  */
	private Integer pingExpires;
	/**  */
	private String userAgent;
	/**  */
	private String serverUser;
	/**  */
	private String serverHost;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private String networkIp;
	/**  */
	private String networkPort;
	/**  */
	private String sipUsername;
	/**  */
	private String sipRealm;
	/**  */
	private String mwiUser;
	/**  */
	private String mwiHost;
	/**  */
	private String origServerHost;
	/**  */
	private String origHostname;
	/**  */
	private String subHost;

	
	/** @取得   */
	public String getCallId(){
		return callId;
	}
	
	/** @设置   */
	public void setCallId(String callId){
		this.callId = callId;
	}
	
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
	public String getPresenceHosts(){
		return presenceHosts;
	}
	
	/** @设置   */
	public void setPresenceHosts(String presenceHosts){
		this.presenceHosts = presenceHosts;
	}
	
	/** @取得   */
	public String getContact(){
		return contact;
	}
	
	/** @设置   */
	public void setContact(String contact){
		this.contact = contact;
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
	public String getPingStatus(){
		return pingStatus;
	}
	
	/** @设置   */
	public void setPingStatus(String pingStatus){
		this.pingStatus = pingStatus;
	}
	
	/** @取得   */
	public Integer getPingCount(){
		return pingCount;
	}
	
	/** @设置   */
	public void setPingCount(Integer pingCount){
		this.pingCount = pingCount;
	}
	
	/** @取得   */
	public Long getPingTime(){
		return pingTime;
	}
	
	/** @设置   */
	public void setPingTime(Long pingTime){
		this.pingTime = pingTime;
	}
	
	/** @取得   */
	public Integer getForcePing(){
		return forcePing;
	}
	
	/** @设置   */
	public void setForcePing(Integer forcePing){
		this.forcePing = forcePing;
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
	public Integer getPingExpires(){
		return pingExpires;
	}
	
	/** @设置   */
	public void setPingExpires(Integer pingExpires){
		this.pingExpires = pingExpires;
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
	public String getServerUser(){
		return serverUser;
	}
	
	/** @设置   */
	public void setServerUser(String serverUser){
		this.serverUser = serverUser;
	}
	
	/** @取得   */
	public String getServerHost(){
		return serverHost;
	}
	
	/** @设置   */
	public void setServerHost(String serverHost){
		this.serverHost = serverHost;
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
	public String getSipUsername(){
		return sipUsername;
	}
	
	/** @设置   */
	public void setSipUsername(String sipUsername){
		this.sipUsername = sipUsername;
	}
	
	/** @取得   */
	public String getSipRealm(){
		return sipRealm;
	}
	
	/** @设置   */
	public void setSipRealm(String sipRealm){
		this.sipRealm = sipRealm;
	}
	
	/** @取得   */
	public String getMwiUser(){
		return mwiUser;
	}
	
	/** @设置   */
	public void setMwiUser(String mwiUser){
		this.mwiUser = mwiUser;
	}
	
	/** @取得   */
	public String getMwiHost(){
		return mwiHost;
	}
	
	/** @设置   */
	public void setMwiHost(String mwiHost){
		this.mwiHost = mwiHost;
	}
	
	/** @取得   */
	public String getOrigServerHost(){
		return origServerHost;
	}
	
	/** @设置   */
	public void setOrigServerHost(String origServerHost){
		this.origServerHost = origServerHost;
	}
	
	/** @取得   */
	public String getOrigHostname(){
		return origHostname;
	}
	
	/** @设置   */
	public void setOrigHostname(String origHostname){
		this.origHostname = origHostname;
	}
	
	/** @取得   */
	public String getSubHost(){
		return subHost;
	}
	
	/** @设置   */
	public void setSubHost(String subHost){
		this.subHost = subHost;
	}

}
