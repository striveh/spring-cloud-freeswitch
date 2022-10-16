/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【nat 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class NatPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private Integer sticky;
	/**  */
	private Integer port;
	/**  */
	private Integer proto;
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
	public Integer getPort(){
		return port;
	}
	
	/** @设置   */
	public void setPort(Integer port){
		this.port = port;
	}
	
	/** @取得   */
	public Integer getProto(){
		return proto;
	}
	
	/** @设置   */
	public void setProto(Integer proto){
		this.proto = proto;
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
