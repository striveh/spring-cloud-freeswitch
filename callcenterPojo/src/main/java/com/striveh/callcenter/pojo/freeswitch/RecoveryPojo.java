/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【recovery 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class RecoveryPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String runtimeUuid;
	/**  */
	private String technology;
	/**  */
	private String profileName;
	/**  */
	private String hostname;
	/**  */
	private String uuid;
	/**  */
	private String metadata;

	
	/** @取得   */
	public String getRuntimeUuid(){
		return runtimeUuid;
	}
	
	/** @设置   */
	public void setRuntimeUuid(String runtimeUuid){
		this.runtimeUuid = runtimeUuid;
	}
	
	/** @取得   */
	public String getTechnology(){
		return technology;
	}
	
	/** @设置   */
	public void setTechnology(String technology){
		this.technology = technology;
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
	public String getUuid(){
		return uuid;
	}
	
	/** @设置   */
	public void setUuid(String uuid){
		this.uuid = uuid;
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
