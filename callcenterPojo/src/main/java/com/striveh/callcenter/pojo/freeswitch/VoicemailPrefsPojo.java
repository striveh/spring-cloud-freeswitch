/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【voicemail_prefs 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class VoicemailPrefsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String username;
	/**  */
	private String domain;
	/**  */
	private String namePath;
	/**  */
	private String greetingPath;
	/**  */
	private String password;

	
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
	public String getNamePath(){
		return namePath;
	}
	
	/** @设置   */
	public void setNamePath(String namePath){
		this.namePath = namePath;
	}
	
	/** @取得   */
	public String getGreetingPath(){
		return greetingPath;
	}
	
	/** @设置   */
	public void setGreetingPath(String greetingPath){
		this.greetingPath = greetingPath;
	}
	
	/** @取得   */
	public String getPassword(){
		return password;
	}
	
	/** @设置   */
	public void setPassword(String password){
		this.password = password;
	}

}
