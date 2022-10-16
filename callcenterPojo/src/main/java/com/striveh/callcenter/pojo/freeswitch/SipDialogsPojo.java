/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【sip_dialogs 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class SipDialogsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String callId;
	/**  */
	private String uuid;
	/**  */
	private String sipToUser;
	/**  */
	private String sipToHost;
	/**  */
	private String sipFromUser;
	/**  */
	private String sipFromHost;
	/**  */
	private String contactUser;
	/**  */
	private String contactHost;
	/**  */
	private String state;
	/**  */
	private String direction;
	/**  */
	private String userAgent;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private String contact;
	/**  */
	private String presenceId;
	/**  */
	private String presenceData;
	/**  */
	private String callInfo;
	/**  */
	private String callInfoState;
	/**  */
	private Long expires;
	/**  */
	private String status;
	/**  */
	private String rpid;
	/**  */
	private String sipToTag;
	/**  */
	private String sipFromTag;
	/**  */
	private Integer rcd;

	
	/** @取得   */
	public String getCallId(){
		return callId;
	}
	
	/** @设置   */
	public void setCallId(String callId){
		this.callId = callId;
	}
	
	/** @取得   */
	public String getUuid(){
		return uuid;
	}
	
	/** @设置   */
	public void setUuid(String uuid){
		this.uuid = uuid;
	}
	
	/** @取得   */
	public String getSipToUser(){
		return sipToUser;
	}
	
	/** @设置   */
	public void setSipToUser(String sipToUser){
		this.sipToUser = sipToUser;
	}
	
	/** @取得   */
	public String getSipToHost(){
		return sipToHost;
	}
	
	/** @设置   */
	public void setSipToHost(String sipToHost){
		this.sipToHost = sipToHost;
	}
	
	/** @取得   */
	public String getSipFromUser(){
		return sipFromUser;
	}
	
	/** @设置   */
	public void setSipFromUser(String sipFromUser){
		this.sipFromUser = sipFromUser;
	}
	
	/** @取得   */
	public String getSipFromHost(){
		return sipFromHost;
	}
	
	/** @设置   */
	public void setSipFromHost(String sipFromHost){
		this.sipFromHost = sipFromHost;
	}
	
	/** @取得   */
	public String getContactUser(){
		return contactUser;
	}
	
	/** @设置   */
	public void setContactUser(String contactUser){
		this.contactUser = contactUser;
	}
	
	/** @取得   */
	public String getContactHost(){
		return contactHost;
	}
	
	/** @设置   */
	public void setContactHost(String contactHost){
		this.contactHost = contactHost;
	}
	
	/** @取得   */
	public String getState(){
		return state;
	}
	
	/** @设置   */
	public void setState(String state){
		this.state = state;
	}
	
	/** @取得   */
	public String getDirection(){
		return direction;
	}
	
	/** @设置   */
	public void setDirection(String direction){
		this.direction = direction;
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
	public String getContact(){
		return contact;
	}
	
	/** @设置   */
	public void setContact(String contact){
		this.contact = contact;
	}
	
	/** @取得   */
	public String getPresenceId(){
		return presenceId;
	}
	
	/** @设置   */
	public void setPresenceId(String presenceId){
		this.presenceId = presenceId;
	}
	
	/** @取得   */
	public String getPresenceData(){
		return presenceData;
	}
	
	/** @设置   */
	public void setPresenceData(String presenceData){
		this.presenceData = presenceData;
	}
	
	/** @取得   */
	public String getCallInfo(){
		return callInfo;
	}
	
	/** @设置   */
	public void setCallInfo(String callInfo){
		this.callInfo = callInfo;
	}
	
	/** @取得   */
	public String getCallInfoState(){
		return callInfoState;
	}
	
	/** @设置   */
	public void setCallInfoState(String callInfoState){
		this.callInfoState = callInfoState;
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
	public String getSipToTag(){
		return sipToTag;
	}
	
	/** @设置   */
	public void setSipToTag(String sipToTag){
		this.sipToTag = sipToTag;
	}
	
	/** @取得   */
	public String getSipFromTag(){
		return sipFromTag;
	}
	
	/** @设置   */
	public void setSipFromTag(String sipFromTag){
		this.sipFromTag = sipFromTag;
	}
	
	/** @取得   */
	public Integer getRcd(){
		return rcd;
	}
	
	/** @设置   */
	public void setRcd(Integer rcd){
		this.rcd = rcd;
	}

}
