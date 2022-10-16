/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【group_data 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:02
 * @说明：<pre></pre>
 */
public class GroupDataPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String hostname;
	/**  */
	private String groupname;
	/**  */
	private String url;

	
	/** @取得   */
	public String getHostname(){
		return hostname;
	}
	
	/** @设置   */
	public void setHostname(String hostname){
		this.hostname = hostname;
	}
	
	/** @取得   */
	public String getGroupname(){
		return groupname;
	}
	
	/** @设置   */
	public void setGroupname(String groupname){
		this.groupname = groupname;
	}
	
	/** @取得   */
	public String getUrl(){
		return url;
	}
	
	/** @设置   */
	public void setUrl(String url){
		this.url = url;
	}

}
