/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【registrations 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class RegistrationsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String regUser;
	/**  */
	private String realm;
	/**  */
	private String token;
	/**  */
	private String url;
	/**  */
	private Integer expires;
	/**  */
	private String networkIp;
	/**  */
	private String networkPort;
	/**  */
	private String networkProto;
	/**  */
	private String hostname;
	/**  */
	private String metadata;

	
	/** @取得   */
	public String getRegUser(){
		return regUser;
	}
	
	/** @设置   */
	public void setRegUser(String regUser){
		this.regUser = regUser;
	}
	
	/** @取得   */
	public String getRealm(){
		return realm;
	}
	
	/** @设置   */
	public void setRealm(String realm){
		this.realm = realm;
	}
	
	/** @取得   */
	public String getToken(){
		return token;
	}
	
	/** @设置   */
	public void setToken(String token){
		this.token = token;
	}
	
	/** @取得   */
	public String getUrl(){
		return url;
	}
	
	/** @设置   */
	public void setUrl(String url){
		this.url = url;
	}
	
	/** @取得   */
	public Integer getExpires(){
		return expires;
	}
	
	/** @设置   */
	public void setExpires(Integer expires){
		this.expires = expires;
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
	public String getNetworkProto(){
		return networkProto;
	}
	
	/** @设置   */
	public void setNetworkProto(String networkProto){
		this.networkProto = networkProto;
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
	public String getMetadata(){
		return metadata;
	}
	
	/** @设置   */
	public void setMetadata(String metadata){
		this.metadata = metadata;
	}

}
