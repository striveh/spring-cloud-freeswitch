/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【tiers 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-13 21:56:43
 * @说明：<pre></pre>
 */
public class TiersPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String queue;
	/**  */
	private String agent;
	/**  */
	private String state;
	/**  */
	private Integer level;
	/**  */
	private Integer position;

	
	/** @取得   */
	public String getQueue(){
		return queue;
	}
	
	/** @设置   */
	public void setQueue(String queue){
		this.queue = queue;
	}
	
	/** @取得   */
	public String getAgent(){
		return agent;
	}
	
	/** @设置   */
	public void setAgent(String agent){
		this.agent = agent;
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
	public Integer getLevel(){
		return level;
	}
	
	/** @设置   */
	public void setLevel(Integer level){
		this.level = level;
	}
	
	/** @取得   */
	public Integer getPosition(){
		return position;
	}
	
	/** @设置   */
	public void setPosition(Integer position){
		this.position = position;
	}

}
