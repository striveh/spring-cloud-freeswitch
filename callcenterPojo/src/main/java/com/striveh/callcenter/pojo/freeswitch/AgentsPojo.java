/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【agents 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-13 21:56:42
 * @说明：<pre></pre>
 */
public class AgentsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String name;
	/**  */
	private String system;
	/**  */
	private String uuid;
	/**  */
	private String type;
	/**  */
	private String contact;
	/**  */
	private String status;
	/**  */
	private String state;
	/**  */
	private Integer maxNoAnswer;
	/**  */
	private Integer wrapUpTime;
	/**  */
	private Integer rejectDelayTime;
	/**  */
	private Integer busyDelayTime;
	/**  */
	private Integer noAnswerDelayTime;
	/**  */
	private Integer lastBridgeStart;
	/**  */
	private Integer lastBridgeEnd;
	/**  */
	private Integer lastOfferedCall;
	/**  */
	private Integer lastStatusChange;
	/**  */
	private Integer noAnswerCount;
	/**  */
	private Integer callsAnswered;
	/**  */
	private Integer talkTime;
	/**  */
	private Integer readyTime;
	/**  */
	private Integer externalCallsCount;

	
	/** @取得   */
	public String getName(){
		return name;
	}
	
	/** @设置   */
	public void setName(String name){
		this.name = name;
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
	public String getType(){
		return type;
	}
	
	/** @设置   */
	public void setType(String type){
		this.type = type;
	}
	
	/** @取得   */
	public String getContact(){
		return contact;
	}
	
	/** @设置   */
	public void setContact(String contact){
		this.contact = contact;
	}
	
	/** @取得   */
	public String getStatus(){
		return status;
	}
	
	/** @设置   */
	public void setStatus(String status){
		this.status = status;
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
	public Integer getMaxNoAnswer(){
		return maxNoAnswer;
	}
	
	/** @设置   */
	public void setMaxNoAnswer(Integer maxNoAnswer){
		this.maxNoAnswer = maxNoAnswer;
	}
	
	/** @取得   */
	public Integer getWrapUpTime(){
		return wrapUpTime;
	}
	
	/** @设置   */
	public void setWrapUpTime(Integer wrapUpTime){
		this.wrapUpTime = wrapUpTime;
	}
	
	/** @取得   */
	public Integer getRejectDelayTime(){
		return rejectDelayTime;
	}
	
	/** @设置   */
	public void setRejectDelayTime(Integer rejectDelayTime){
		this.rejectDelayTime = rejectDelayTime;
	}
	
	/** @取得   */
	public Integer getBusyDelayTime(){
		return busyDelayTime;
	}
	
	/** @设置   */
	public void setBusyDelayTime(Integer busyDelayTime){
		this.busyDelayTime = busyDelayTime;
	}
	
	/** @取得   */
	public Integer getNoAnswerDelayTime(){
		return noAnswerDelayTime;
	}
	
	/** @设置   */
	public void setNoAnswerDelayTime(Integer noAnswerDelayTime){
		this.noAnswerDelayTime = noAnswerDelayTime;
	}
	
	/** @取得   */
	public Integer getLastBridgeStart(){
		return lastBridgeStart;
	}
	
	/** @设置   */
	public void setLastBridgeStart(Integer lastBridgeStart){
		this.lastBridgeStart = lastBridgeStart;
	}
	
	/** @取得   */
	public Integer getLastBridgeEnd(){
		return lastBridgeEnd;
	}
	
	/** @设置   */
	public void setLastBridgeEnd(Integer lastBridgeEnd){
		this.lastBridgeEnd = lastBridgeEnd;
	}
	
	/** @取得   */
	public Integer getLastOfferedCall(){
		return lastOfferedCall;
	}
	
	/** @设置   */
	public void setLastOfferedCall(Integer lastOfferedCall){
		this.lastOfferedCall = lastOfferedCall;
	}
	
	/** @取得   */
	public Integer getLastStatusChange(){
		return lastStatusChange;
	}
	
	/** @设置   */
	public void setLastStatusChange(Integer lastStatusChange){
		this.lastStatusChange = lastStatusChange;
	}
	
	/** @取得   */
	public Integer getNoAnswerCount(){
		return noAnswerCount;
	}
	
	/** @设置   */
	public void setNoAnswerCount(Integer noAnswerCount){
		this.noAnswerCount = noAnswerCount;
	}
	
	/** @取得   */
	public Integer getCallsAnswered(){
		return callsAnswered;
	}
	
	/** @设置   */
	public void setCallsAnswered(Integer callsAnswered){
		this.callsAnswered = callsAnswered;
	}
	
	/** @取得   */
	public Integer getTalkTime(){
		return talkTime;
	}
	
	/** @设置   */
	public void setTalkTime(Integer talkTime){
		this.talkTime = talkTime;
	}
	
	/** @取得   */
	public Integer getReadyTime(){
		return readyTime;
	}
	
	/** @设置   */
	public void setReadyTime(Integer readyTime){
		this.readyTime = readyTime;
	}
	
	/** @取得   */
	public Integer getExternalCallsCount(){
		return externalCallsCount;
	}
	
	/** @设置   */
	public void setExternalCallsCount(Integer externalCallsCount){
		this.externalCallsCount = externalCallsCount;
	}

}
