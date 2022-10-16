/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【interfaces 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class InterfacesPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String type;
	/**  */
	private String name;
	/**  */
	private String description;
	/**  */
	private String ikey;
	/**  */
	private String filename;
	/**  */
	private String syntax;
	/**  */
	private String hostname;

	
	/** @取得   */
	public String getType(){
		return type;
	}
	
	/** @设置   */
	public void setType(String type){
		this.type = type;
	}
	
	/** @取得   */
	public String getName(){
		return name;
	}
	
	/** @设置   */
	public void setName(String name){
		this.name = name;
	}
	
	/** @取得   */
	public String getDescription(){
		return description;
	}
	
	/** @设置   */
	public void setDescription(String description){
		this.description = description;
	}
	
	/** @取得   */
	public String getIkey(){
		return ikey;
	}
	
	/** @设置   */
	public void setIkey(String ikey){
		this.ikey = ikey;
	}
	
	/** @取得   */
	public String getFilename(){
		return filename;
	}
	
	/** @设置   */
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	/** @取得   */
	public String getSyntax(){
		return syntax;
	}
	
	/** @设置   */
	public void setSyntax(String syntax){
		this.syntax = syntax;
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
