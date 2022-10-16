/** */
package com.striveh.callcenter.pojo.freeswitch;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【cdr 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:01
 * @说明：<pre></pre>
 */
public class CdrPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String callerIdName;
	/**  */
	private String callerIdNumber;
	/**  */
	private String destinationNumber;
	/**  */
	private String context;
	/**  */
	private Timestamp startTimestamp;
	/**  */
	private Timestamp answerTimestamp;
	/**  */
	private Timestamp endTimestamp;
	/**  */
	private Integer duration;
	/**  */
	private Integer billsec;
	/**  */
	private String hangupCause;
	/**  */
	private String uuid;
	/**  */
	private String blegUuid;
	/**  */
	private String accountcode;
	/**  */
	private String readCodec;
	/**  */
	private String writeCodec;
	/**  */
	private String sipGatewayName;
	/** 项目代码 */
	private String projectCode;
	/** 任务代码 */
	private String callTaskCode;

	
	/** @取得   */
	public String getCallerIdName(){
		return callerIdName;
	}
	
	/** @设置   */
	public void setCallerIdName(String callerIdName){
		this.callerIdName = callerIdName;
	}
	
	/** @取得   */
	public String getCallerIdNumber(){
		return callerIdNumber;
	}
	
	/** @设置   */
	public void setCallerIdNumber(String callerIdNumber){
		this.callerIdNumber = callerIdNumber;
	}
	
	/** @取得   */
	public String getDestinationNumber(){
		return destinationNumber;
	}
	
	/** @设置   */
	public void setDestinationNumber(String destinationNumber){
		this.destinationNumber = destinationNumber;
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
	public Timestamp getStartTimestamp(){
		return startTimestamp;
	}
	
	/** @设置   */
	public void setStartTimestamp(Timestamp startTimestamp){
		this.startTimestamp = startTimestamp;
	}
	
	/** @取得   */
	public Timestamp getAnswerTimestamp(){
		return answerTimestamp;
	}
	
	/** @设置   */
	public void setAnswerTimestamp(Timestamp answerTimestamp){
		this.answerTimestamp = answerTimestamp;
	}
	
	/** @取得   */
	public Timestamp getEndTimestamp(){
		return endTimestamp;
	}
	
	/** @设置   */
	public void setEndTimestamp(Timestamp endTimestamp){
		this.endTimestamp = endTimestamp;
	}
	
	/** @取得   */
	public Integer getDuration(){
		return duration;
	}
	
	/** @设置   */
	public void setDuration(Integer duration){
		this.duration = duration;
	}
	
	/** @取得   */
	public Integer getBillsec(){
		return billsec;
	}
	
	/** @设置   */
	public void setBillsec(Integer billsec){
		this.billsec = billsec;
	}
	
	/** @取得   */
	public String getHangupCause(){
		return hangupCause;
	}
	
	/** @设置   */
	public void setHangupCause(String hangupCause){
		this.hangupCause = hangupCause;
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
	public String getBlegUuid(){
		return blegUuid;
	}
	
	/** @设置   */
	public void setBlegUuid(String blegUuid){
		this.blegUuid = blegUuid;
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
	public String getReadCodec(){
		return readCodec;
	}
	
	/** @设置   */
	public void setReadCodec(String readCodec){
		this.readCodec = readCodec;
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
	public String getSipGatewayName(){
		return sipGatewayName;
	}
	
	/** @设置   */
	public void setSipGatewayName(String sipGatewayName){
		this.sipGatewayName = sipGatewayName;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getCallTaskCode() {
		return callTaskCode;
	}

	public void setCallTaskCode(String callTaskCode) {
		this.callTaskCode = callTaskCode;
	}
}
