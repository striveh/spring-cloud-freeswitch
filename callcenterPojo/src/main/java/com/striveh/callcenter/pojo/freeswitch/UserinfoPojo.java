/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

import java.sql.Timestamp;

/**
 * @功能:【userinfo 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class UserinfoPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private String username;
	/**  */
	private String password;

	/**  */
	private String servers;
	private String nativeServers;
	/**  */
	private String uri;

	private Long projectId;

	/** -1不可用、0不可通话、1可通话 */
	private Integer workStatus;
	/** 0未注册、1已注册 */
	private Integer status;

	private String calluuid;

	/** 最新一次通话开始时间 */
	private Timestamp lastSessionBeginTime;
	/** 最新一次通话手机号 */
	private String destinationNumber;
	/** 任务代码 */
	private String callTaskCode;
	/** 0未通话、7会议中、10通话中 */
	private Integer sessionStatus;

	/** 通话中的唯一标识号 */
	private String destinationUUID;
	private Long freeswitchId;
	/** 1语音网关2坐席顾问3项目经理 */
	private Integer type;
	
	/** @取得   */
	public String getUsername(){
		return username;
	}
	
	/** @设置   */
	public void setUsername(String username){
		this.username = username;
	}
	
	/** @取得   */
	public String getPassword(){
		return password;
	}
	
	/** @设置   */
	public void setPassword(String password){
		this.password = password;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(Integer workStatus) {
		this.workStatus = workStatus;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCalluuid() {
		return calluuid;
	}

	public void setCalluuid(String calluuid) {
		this.calluuid = calluuid;
	}

	public String getNativeServers() {
		return nativeServers;
	}

	public void setNativeServers(String nativeServers) {
		this.nativeServers = nativeServers;
	}

	public Timestamp getLastSessionBeginTime() {
		return lastSessionBeginTime;
	}

	public void setLastSessionBeginTime(Timestamp lastSessionBeginTime) {
		this.lastSessionBeginTime = lastSessionBeginTime;
	}

	public Integer getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(Integer sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getDestinationNumber() {
		return destinationNumber;
	}

	public void setDestinationNumber(String destinationNumber) {
		this.destinationNumber = destinationNumber;
	}

	public String getCallTaskCode() {
		return callTaskCode;
	}

	public void setCallTaskCode(String callTaskCode) {
		this.callTaskCode = callTaskCode;
	}

	public String getDestinationUUID() {
		return destinationUUID;
	}

	public void setDestinationUUID(String destinationUUID) {
		this.destinationUUID = destinationUUID;
	}

	public Long getFreeswitchId() {
		return freeswitchId;
	}

	public void setFreeswitchId(Long freeswitchId) {
		this.freeswitchId = freeswitchId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
