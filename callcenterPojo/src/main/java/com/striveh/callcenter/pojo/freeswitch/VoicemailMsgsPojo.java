/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【voicemail_msgs 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class VoicemailMsgsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private Integer createdEpoch;
	/**  */
	private Integer readEpoch;
	/**  */
	private String username;
	/**  */
	private String domain;
	/**  */
	private String uuid;
	/**  */
	private String cidName;
	/**  */
	private String cidNumber;
	/**  */
	private String inFolder;
	/**  */
	private String filePath;
	/**  */
	private Integer messageLen;
	/**  */
	private String flags;
	/**  */
	private String readFlags;
	/**  */
	private String forwardedBy;

	
	/** @取得   */
	public Integer getCreatedEpoch(){
		return createdEpoch;
	}
	
	/** @设置   */
	public void setCreatedEpoch(Integer createdEpoch){
		this.createdEpoch = createdEpoch;
	}
	
	/** @取得   */
	public Integer getReadEpoch(){
		return readEpoch;
	}
	
	/** @设置   */
	public void setReadEpoch(Integer readEpoch){
		this.readEpoch = readEpoch;
	}
	
	/** @取得   */
	public String getUsername(){
		return username;
	}
	
	/** @设置   */
	public void setUsername(String username){
		this.username = username;
	}
	
	/** @取得   */
	public String getDomain(){
		return domain;
	}
	
	/** @设置   */
	public void setDomain(String domain){
		this.domain = domain;
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
	public String getCidName(){
		return cidName;
	}
	
	/** @设置   */
	public void setCidName(String cidName){
		this.cidName = cidName;
	}
	
	/** @取得   */
	public String getCidNumber(){
		return cidNumber;
	}
	
	/** @设置   */
	public void setCidNumber(String cidNumber){
		this.cidNumber = cidNumber;
	}
	
	/** @取得   */
	public String getInFolder(){
		return inFolder;
	}
	
	/** @设置   */
	public void setInFolder(String inFolder){
		this.inFolder = inFolder;
	}
	
	/** @取得   */
	public String getFilePath(){
		return filePath;
	}
	
	/** @设置   */
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	
	/** @取得   */
	public Integer getMessageLen(){
		return messageLen;
	}
	
	/** @设置   */
	public void setMessageLen(Integer messageLen){
		this.messageLen = messageLen;
	}
	
	/** @取得   */
	public String getFlags(){
		return flags;
	}
	
	/** @设置   */
	public void setFlags(String flags){
		this.flags = flags;
	}
	
	/** @取得   */
	public String getReadFlags(){
		return readFlags;
	}
	
	/** @设置   */
	public void setReadFlags(String readFlags){
		this.readFlags = readFlags;
	}
	
	/** @取得   */
	public String getForwardedBy(){
		return forwardedBy;
	}
	
	/** @设置   */
	public void setForwardedBy(String forwardedBy){
		this.forwardedBy = forwardedBy;
	}

}
