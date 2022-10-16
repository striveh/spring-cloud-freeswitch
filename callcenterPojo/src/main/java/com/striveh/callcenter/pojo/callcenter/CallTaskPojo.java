/** */
package com.striveh.callcenter.pojo.callcenter;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【callTask 呼叫任务表】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 12:14:14
 * @说明：<pre></pre>
 */
public class CallTaskPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 项目代码 */
	private String projectCode;
	/** 任务代码 */
	private String callTaskCode;
	/** 系统调拨类型，1直接转坐席2播放录音后转坐席3先接坐席再拨客户4摘机坐席 */
	private Integer scheduleType;
	/** 拨打类型，1预测式拨打2预览式拨打 */
	private Integer callType;
	/** 线路列表[{"code":"1001","concurrent":100},{"code":"1002","concurrent":200}] */
	private String callGWs;
	/** 呼叫号码列表ID */
	private Long callListId;
	/** 过期时间 */
	private Timestamp expiredTime;
	/** 添加时间 */
	private Timestamp addTime;
	/** 更新时间 */
	private Timestamp updateTime;
	/** 状态1初始化2启动3暂定4结束 */
	private Integer status;
	/** 拨打倍率 */
	private Double rate;
	/** 成功数 */
	private Integer successCount;
	/** 失败数 */
	private Integer failCount;
	/** 漏接数 */
	private Integer missCount;
	/** 呼叫列表总数 */
	private Integer totalCount;

	private String originalResultDetail;
	private String originalCallTaskCode;
	private Long callListLastId;

	private String voiceCode;

	
	/** @取得  项目代码 */
	public String getProjectCode(){
		return projectCode;
	}
	
	/** @设置  项目代码 */
	public void setProjectCode(String projectCode){
		this.projectCode = projectCode;
	}
	
	/** @取得  任务代码 */
	public String getCallTaskCode(){
		return callTaskCode;
	}
	
	/** @设置  任务代码 */
	public void setCallTaskCode(String callTaskCode){
		this.callTaskCode = callTaskCode;
	}
	
	/** @取得  系统调拨类型，1直接转坐席2播放录音后转坐席3先接坐席再拨客户4摘机坐席 */
	public Integer getScheduleType(){
		return scheduleType;
	}
	
	/** @设置  系统调拨类型，1直接转坐席2播放录音后转坐席3先接坐席再拨客户4摘机坐席 */
	public void setScheduleType(Integer scheduleType){
		this.scheduleType = scheduleType;
	}
	
	/** @取得  拨打类型，1预测式拨打2预览式拨打 */
	public Integer getCallType(){
		return callType;
	}
	
	/** @设置  拨打类型，1预测式拨打2预览式拨打 */
	public void setCallType(Integer callType){
		this.callType = callType;
	}
	
	/** @取得  线路列表[{"code":"1001","concurrent":100},{"code":"1002","concurrent":200}] */
	public String getCallGWs(){
		return callGWs;
	}
	
	/** @设置  线路列表[{"code":"1001","concurrent":100},{"code":"1002","concurrent":200}] */
	public void setCallGWs(String callGWs){
		this.callGWs = callGWs;
	}
	
	/** @取得  呼叫号码列表ID */
	public Long getCallListId(){
		return callListId;
	}
	
	/** @设置  呼叫号码列表ID */
	public void setCallListId(Long callListId){
		this.callListId = callListId;
	}
	
	/** @取得  过期时间 */
	public Timestamp getExpiredTime(){
		return expiredTime;
	}
	
	/** @设置  过期时间 */
	public void setExpiredTime(Timestamp expiredTime){
		this.expiredTime = expiredTime;
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
	
	/** @取得  状态1初始化2启动3暂定4结束 */
	public Integer getStatus(){
		return status;
	}
	
	/** @设置  状态1初始化2启动3暂定4结束 */
	public void setStatus(Integer status){
		this.status = status;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public Integer getMissCount() {
		return missCount;
	}

	public void setMissCount(Integer missCount) {
		this.missCount = missCount;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public String getOriginalResultDetail() {
		return originalResultDetail;
	}

	public void setOriginalResultDetail(String originalResultDetail) {
		this.originalResultDetail = originalResultDetail;
	}

	public String getOriginalCallTaskCode() {
		return originalCallTaskCode;
	}

	public void setOriginalCallTaskCode(String originalCallTaskCode) {
		this.originalCallTaskCode = originalCallTaskCode;
	}

	public Long getCallListLastId() {
		return callListLastId;
	}

	public void setCallListLastId(Long callListLastId) {
		this.callListLastId = callListLastId;
	}

	public String getVoiceCode() {
		return voiceCode;
	}

	public void setVoiceCode(String voiceCode) {
		this.voiceCode = voiceCode;
	}
}
