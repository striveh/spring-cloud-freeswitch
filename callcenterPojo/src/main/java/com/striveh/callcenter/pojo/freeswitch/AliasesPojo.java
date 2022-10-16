/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【aliases 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:01
 * @说明：<pre></pre>
 */
public class AliasesPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private Integer sticky;
	/**  */
	private String alias;
	/**  */
	private String command;
	/**  */
	private String hostname;

	
	/** @取得   */
	public Integer getSticky(){
		return sticky;
	}
	
	/** @设置   */
	public void setSticky(Integer sticky){
		this.sticky = sticky;
	}
	
	/** @取得   */
	public String getAlias(){
		return alias;
	}
	
	/** @设置   */
	public void setAlias(String alias){
		this.alias = alias;
	}
	
	/** @取得   */
	public String getCommand(){
		return command;
	}
	
	/** @设置   */
	public void setCommand(String command){
		this.command = command;
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
