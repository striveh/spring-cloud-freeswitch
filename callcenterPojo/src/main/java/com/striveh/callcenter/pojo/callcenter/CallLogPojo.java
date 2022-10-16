/** */
package com.striveh.callcenter.pojo.callcenter;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【callLog 呼叫日志表】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 12:14:14
 * @说明：<pre></pre>
 */
public class CallLogPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** callUUID */
	private String callUUID;
	/** 主叫号码 */
	private String callerIdNumber;
	/** 被叫号码 */
	private String destinationNumber;
	/** 录音文件在文件服务器的地址 */
	private String recordFile;
	private String recordFileQiniu;
	/** 开始时间 */
	private Timestamp startTimestamp;
	/** 结束时间 */
	private Timestamp endTimestamp;
	/** 持续时间,单位秒 */
	private Long duration;
	/** 计费时长,单位秒 */
	private Long billsec;
	/** 添加时间 */
	private Timestamp addTime;
	/** 更新时间 */
	private Timestamp updateTime;
	/** 呼叫结果 */
	private String result;
	private String[] results;
	/** 呼叫结果详情 */
	private String resultDetail;
	private String[] resultDetails;
	/** 项目代码 */
	private String projectCode;
	/** 任务代码 */
	private String callTaskCode;
	/** 网关代码 */
	private String gwCode;
	/** 每多少秒多少钱[用|隔开] 格式：50|0.6 */
	private String priceRule;
	/** 实际计算价格 **/
	private Double priceCal;
	/** FS外呼结果 */
	private String hangupCase;
	/** 用户满意度评分，1非常满意2满意3不满意 */
	private Integer evaluation;


	/** 查询开始时间 */
	private Timestamp beginTime;
	/** 查询结束时间 */
	private Timestamp endTime;
	private Integer queryTimeType;

	/** 重呼时返回的重呼呼叫列表的最后一条记录的ID */
	private Long recallLastId;

	
	/** @取得  callUUID */
	public String getCallUUID(){
		return callUUID;
	}
	
	/** @设置  callUUID */
	public void setCallUUID(String callUUID){
		this.callUUID = callUUID;
	}
	
	/** @取得  主叫号码 */
	public String getCallerIdNumber(){
		return callerIdNumber;
	}
	
	/** @设置  主叫号码 */
	public void setCallerIdNumber(String callerIdNumber){
		this.callerIdNumber = callerIdNumber;
	}
	
	/** @取得  被叫号码 */
	public String getDestinationNumber(){
		return destinationNumber;
	}
	
	/** @设置  被叫号码 */
	public void setDestinationNumber(String destinationNumber){
		this.destinationNumber = destinationNumber;
	}
	
	/** @取得  呼叫结果 */
	public String getResult(){
		return result;
	}
	
	/** @设置  呼叫结果 */
	public void setResult(String result){
		this.result = result;
	}
	
	/** @取得  录音文件在文件服务器的地址 */
	public String getRecordFile(){
		return recordFile;
	}
	
	/** @设置  录音文件在文件服务器的地址 */
	public void setRecordFile(String recordFile){
		this.recordFile = recordFile;
	}
	
	/** @取得  开始时间 */
	public Timestamp getStartTimestamp(){
		return startTimestamp;
	}
	
	/** @设置  开始时间 */
	public void setStartTimestamp(Timestamp startTimestamp){
		this.startTimestamp = startTimestamp;
	}
	
	/** @取得  结束时间 */
	public Timestamp getEndTimestamp(){
		return endTimestamp;
	}
	
	/** @设置  结束时间 */
	public void setEndTimestamp(Timestamp endTimestamp){
		this.endTimestamp = endTimestamp;
	}
	
	/** @取得  持续时间,单位秒 */
	public Long getDuration(){
		return duration;
	}
	
	/** @设置  持续时间,单位秒 */
	public void setDuration(Long duration){
		this.duration = duration;
	}
	
	/** @取得  添加时间 */
	public Timestamp getAddTime(){
		return addTime;
	}
	
	/** @设置  添加时间 */
	public void setAddTime(Timestamp addTime){
		this.addTime = addTime;
	}
	
	/** @取得  更新时间 */
	public Timestamp getUpdateTime(){
		return updateTime;
	}
	
	/** @设置  更新时间 */
	public void setUpdateTime(Timestamp updateTime){
		this.updateTime = updateTime;
	}

	public String getResultDetail() {
		return resultDetail;
	}

	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
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

	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getGwCode() {
		return gwCode;
	}

	public void setGwCode(String gwCode) {
		this.gwCode = gwCode;
	}

	public String[] getResults() {
		return results;
	}

	public void setResults(String[] results) {
		this.results = results;
	}

	public String[] getResultDetails() {
		return resultDetails;
	}

	public void setResultDetails(String[] resultDetails) {
		this.resultDetails = resultDetails;
	}

	public String getRecordFileQiniu() {
		return recordFileQiniu;
	}

	public void setRecordFileQiniu(String recordFileQiniu) {
		this.recordFileQiniu = recordFileQiniu;
	}

	public String getPriceRule() {
		return priceRule;
	}

	public void setPriceRule(String priceRule) {
		this.priceRule = priceRule;
	}

	public Double getPriceCal() {
		return priceCal;
	}

	public void setPriceCal(Double priceCal) {
		this.priceCal = priceCal;
	}

	public Long getBillsec() {
		return billsec;
	}

	public void setBillsec(Long billsec) {
		this.billsec = billsec;
	}

	public String getHangupCase() {
		return hangupCase;
	}

	public void setHangupCase(String hangupCase) {
		this.hangupCase = hangupCase;
	}

	public Integer getQueryTimeType() {
		return queryTimeType;
	}

	public void setQueryTimeType(Integer queryTimeType) {
		this.queryTimeType = queryTimeType;
	}

	public Integer getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}

	public Long getRecallLastId() {
		return recallLastId;
	}

	public void setRecallLastId(Long recallLastId) {
		this.recallLastId = recallLastId;
	}
}
