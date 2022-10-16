/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【channels 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:01
 * @说明：<pre></pre>
 */
public class ChannelsPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String uuid;
	/**  */
	private String direction;
	/**  */
	private String created;
	/**  */
	private Integer createdEpoch;
	/**  */
	private String name;
	/**  */
	private String state;
	/**  */
	private String cidName;
	/**  */
	private String cidNum;
	/**  */
	private String ipAddr;
	/**  */
	private String dest;
	/**  */
	private String application;
	/**  */
	private String applicationData;
	/**  */
	private String dialplan;
	/**  */
	private String context;
	/**  */
	private String readCodec;
	/**  */
	private String readRate;
	/**  */
	private String readBitRate;
	/**  */
	private String writeCodec;
	/**  */
	private String writeRate;
	/**  */
	private String writeBitRate;
	/**  */
	private String secure;
	/**  */
	private String hostname;
	/**  */
	private String presenceId;
	/**  */
	private String presenceData;
	/**  */
	private String accountcode;
	/**  */
	private String callstate;
	/**  */
	private String calleeName;
	/**  */
	private String calleeNum;
	/**  */
	private String calleeDirection;
	/**  */
	private String callUuid;
	/**  */
	private String sentCalleeName;
	/**  */
	private String sentCalleeNum;
	/**  */
	private String initialCidName;
	/**  */
	private String initialCidNum;
	/**  */
	private String initialIpAddr;
	/**  */
	private String initialDest;
	/**  */
	private String initialDialplan;
	/**  */
	private String initialContext;

	
	/** @取得   */
	public String getUuid(){
		return uuid;
	}
	
	/** @设置   */
	public void setUuid(String uuid){
		this.uuid = uuid;
	}
	
	/** @取得   */
	public String getDirection(){
		return direction;
	}
	
	/** @设置   */
	public void setDirection(String direction){
		this.direction = direction;
	}
	
	/** @取得   */
	public String getCreated(){
		return created;
	}
	
	/** @设置   */
	public void setCreated(String created){
		this.created = created;
	}
	
	/** @取得   */
	public Integer getCreatedEpoch(){
		return createdEpoch;
	}
	
	/** @设置   */
	public void setCreatedEpoch(Integer createdEpoch){
		this.createdEpoch = createdEpoch;
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
	public String getState(){
		return state;
	}
	
	/** @设置   */
	public void setState(String state){
		this.state = state;
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
	public String getCidNum(){
		return cidNum;
	}
	
	/** @设置   */
	public void setCidNum(String cidNum){
		this.cidNum = cidNum;
	}
	
	/** @取得   */
	public String getIpAddr(){
		return ipAddr;
	}
	
	/** @设置   */
	public void setIpAddr(String ipAddr){
		this.ipAddr = ipAddr;
	}
	
	/** @取得   */
	public String getDest(){
		return dest;
	}
	
	/** @设置   */
	public void setDest(String dest){
		this.dest = dest;
	}
	
	/** @取得   */
	public String getApplication(){
		return application;
	}
	
	/** @设置   */
	public void setApplication(String application){
		this.application = application;
	}
	
	/** @取得   */
	public String getApplicationData(){
		return applicationData;
	}
	
	/** @设置   */
	public void setApplicationData(String applicationData){
		this.applicationData = applicationData;
	}
	
	/** @取得   */
	public String getDialplan(){
		return dialplan;
	}
	
	/** @设置   */
	public void setDialplan(String dialplan){
		this.dialplan = dialplan;
	}
	
	/** @取得   */
	public String getContext(){
		return context;
	}
	
	/** @设置   */
	public void setContext(String context){
		this.context = context;
	}
	
	/** @取得   */
	public String getReadCodec(){
		return readCodec;
	}
	
	/** @设置   */
	public void setReadCodec(String readCodec){
		this.readCodec = readCodec;
	}
	
	/** @取得   */
	public String getReadRate(){
		return readRate;
	}
	
	/** @设置   */
	public void setReadRate(String readRate){
		this.readRate = readRate;
	}
	
	/** @取得   */
	public String getReadBitRate(){
		return readBitRate;
	}
	
	/** @设置   */
	public void setReadBitRate(String readBitRate){
		this.readBitRate = readBitRate;
	}
	
	/** @取得   */
	public String getWriteCodec(){
		return writeCodec;
	}
	
	/** @设置   */
	public void setWriteCodec(String writeCodec){
		this.writeCodec = writeCodec;
	}
	
	/** @取得   */
	public String getWriteRate(){
		return writeRate;
	}
	
	/** @设置   */
	public void setWriteRate(String writeRate){
		this.writeRate = writeRate;
	}
	
	/** @取得   */
	public String getWriteBitRate(){
		return writeBitRate;
	}
	
	/** @设置   */
	public void setWriteBitRate(String writeBitRate){
		this.writeBitRate = writeBitRate;
	}
	
	/** @取得   */
	public String getSecure(){
		return secure;
	}
	
	/** @设置   */
	public void setSecure(String secure){
		this.secure = secure;
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
	public String getPresenceId(){
		return presenceId;
	}
	
	/** @设置   */
	public void setPresenceId(String presenceId){
		this.presenceId = presenceId;
	}
	
	/** @取得   */
	public String getPresenceData(){
		return presenceData;
	}
	
	/** @设置   */
	public void setPresenceData(String presenceData){
		this.presenceData = presenceData;
	}
	
	/** @取得   */
	public String getAccountcode(){
		return accountcode;
	}
	
	/** @设置   */
	public void setAccountcode(String accountcode){
		this.accountcode = accountcode;
	}
	
	/** @取得   */
	public String getCallstate(){
		return callstate;
	}
	
	/** @设置   */
	public void setCallstate(String callstate){
		this.callstate = callstate;
	}
	
	/** @取得   */
	public String getCalleeName(){
		return calleeName;
	}
	
	/** @设置   */
	public void setCalleeName(String calleeName){
		this.calleeName = calleeName;
	}
	
	/** @取得   */
	public String getCalleeNum(){
		return calleeNum;
	}
	
	/** @设置   */
	public void setCalleeNum(String calleeNum){
		this.calleeNum = calleeNum;
	}
	
	/** @取得   */
	public String getCalleeDirection(){
		return calleeDirection;
	}
	
	/** @设置   */
	public void setCalleeDirection(String calleeDirection){
		this.calleeDirection = calleeDirection;
	}
	
	/** @取得   */
	public String getCallUuid(){
		return callUuid;
	}
	
	/** @设置   */
	public void setCallUuid(String callUuid){
		this.callUuid = callUuid;
	}
	
	/** @取得   */
	public String getSentCalleeName(){
		return sentCalleeName;
	}
	
	/** @设置   */
	public void setSentCalleeName(String sentCalleeName){
		this.sentCalleeName = sentCalleeName;
	}
	
	/** @取得   */
	public String getSentCalleeNum(){
		return sentCalleeNum;
	}
	
	/** @设置   */
	public void setSentCalleeNum(String sentCalleeNum){
		this.sentCalleeNum = sentCalleeNum;
	}
	
	/** @取得   */
	public String getInitialCidName(){
		return initialCidName;
	}
	
	/** @设置   */
	public void setInitialCidName(String initialCidName){
		this.initialCidName = initialCidName;
	}
	
	/** @取得   */
	public String getInitialCidNum(){
		return initialCidNum;
	}
	
	/** @设置   */
	public void setInitialCidNum(String initialCidNum){
		this.initialCidNum = initialCidNum;
	}
	
	/** @取得   */
	public String getInitialIpAddr(){
		return initialIpAddr;
	}
	
	/** @设置   */
	public void setInitialIpAddr(String initialIpAddr){
		this.initialIpAddr = initialIpAddr;
	}
	
	/** @取得   */
	public String getInitialDest(){
		return initialDest;
	}
	
	/** @设置   */
	public void setInitialDest(String initialDest){
		this.initialDest = initialDest;
	}
	
	/** @取得   */
	public String getInitialDialplan(){
		return initialDialplan;
	}
	
	/** @设置   */
	public void setInitialDialplan(String initialDialplan){
		this.initialDialplan = initialDialplan;
	}
	
	/** @取得   */
	public String getInitialContext(){
		return initialContext;
	}
	
	/** @设置   */
	public void setInitialContext(String initialContext){
		this.initialContext = initialContext;
	}

}
