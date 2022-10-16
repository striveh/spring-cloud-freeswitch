/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【members 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-13 21:56:43
 * @说明：<pre></pre>
 */
public class MembersPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String queue;
	/**  */
	private String system;
	/**  */
	private String uuid;
	/**  */
	private String sessionUuid;
	/**  */
	private String cidNumber;
	/**  */
	private String cidName;
	/**  */
	private Integer systemEpoch;
	/**  */
	private Integer joinedEpoch;
	/**  */
	private Integer rejoinedEpoch;
	/**  */
	private Integer bridgeEpoch;
	/**  */
	private Integer abandonedEpoch;
	/**  */
	private Integer baseScore;
	/**  */
	private Integer skillScore;
	/**  */
	private String servingAgent;
	/**  */
	private String servingSystem;
	/**  */
	private String state;

	
	/** @取得   */
	public String getQueue(){
		return queue;
	}
	
	/** @设置   */
	public void setQueue(String queue){
		this.queue = queue;
	}
	
	/** @取得   */
	public String getSystem(){
		return system;
	}
	
	/** @设置   */
	public void setSystem(String system){
		this.system = system;
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
	public String getSessionUuid(){
		return sessionUuid;
	}
	
	/** @设置   */
	public void setSessionUuid(String sessionUuid){
		this.sessionUuid = sessionUuid;
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
	public String getCidName(){
		return cidName;
	}
	
	/** @设置   */
	public void setCidName(String cidName){
		this.cidName = cidName;
	}
	
	/** @取得   */
	public Integer getSystemEpoch(){
		return systemEpoch;
	}
	
	/** @设置   */
	public void setSystemEpoch(Integer systemEpoch){
		this.systemEpoch = systemEpoch;
	}
	
	/** @取得   */
	public Integer getJoinedEpoch(){
		return joinedEpoch;
	}
	
	/** @设置   */
	public void setJoinedEpoch(Integer joinedEpoch){
		this.joinedEpoch = joinedEpoch;
	}
	
	/** @取得   */
	public Integer getRejoinedEpoch(){
		return rejoinedEpoch;
	}
	
	/** @设置   */
	public void setRejoinedEpoch(Integer rejoinedEpoch){
		this.rejoinedEpoch = rejoinedEpoch;
	}
	
	/** @取得   */
	public Integer getBridgeEpoch(){
		return bridgeEpoch;
	}
	
	/** @设置   */
	public void setBridgeEpoch(Integer bridgeEpoch){
		this.bridgeEpoch = bridgeEpoch;
	}
	
	/** @取得   */
	public Integer getAbandonedEpoch(){
		return abandonedEpoch;
	}
	
	/** @设置   */
	public void setAbandonedEpoch(Integer abandonedEpoch){
		this.abandonedEpoch = abandonedEpoch;
	}
	
	/** @取得   */
	public Integer getBaseScore(){
		return baseScore;
	}
	
	/** @设置   */
	public void setBaseScore(Integer baseScore){
		this.baseScore = baseScore;
	}
	
	/** @取得   */
	public Integer getSkillScore(){
		return skillScore;
	}
	
	/** @设置   */
	public void setSkillScore(Integer skillScore){
		this.skillScore = skillScore;
	}
	
	/** @取得   */
	public String getServingAgent(){
		return servingAgent;
	}
	
	/** @设置   */
	public void setServingAgent(String servingAgent){
		this.servingAgent = servingAgent;
	}
	
	/** @取得   */
	public String getServingSystem(){
		return servingSystem;
	}
	
	/** @设置   */
	public void setServingSystem(String servingSystem){
		this.servingSystem = servingSystem;
	}
	
	/** @取得   */
	public String getState(){
		return state;
	}
	
	/** @设置   */
	public void setState(String state){
		this.state = state;
	}

}
