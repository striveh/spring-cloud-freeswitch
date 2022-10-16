/** */
package com.striveh.callcenter.pojo.callcenter;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【callProject 呼叫项目表】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 12:14:14
 * @说明：<pre></pre>
 */
public class CallProjectPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 项目代码 */
	private String projectCode;
	/** 坐席数 */
	private Integer seats;
	/** 坐席领取项目分机凭证 */
	private String certificate;
	/** 分配给该项目的分机号段 */
	private String extNumInterval;
	/** 添加时间 */
	private Timestamp addTime;
	/** 更新时间 */
	private Timestamp updateTime;
	/** 状态1进行中2完结 */
	private Integer status;

	private Long freeswitchId;

	private String servers;
	private String nativeServers;

	
	/** @取得  项目代码 */
	public String getProjectCode(){
		return projectCode;
	}
	
	/** @设置  项目代码 */
	public void setProjectCode(String projectCode){
		this.projectCode = projectCode;
	}
	
	/** @取得  坐席数 */
	public Integer getSeats(){
		return seats;
	}
	
	/** @设置  坐席数 */
	public void setSeats(Integer seats){
		this.seats = seats;
	}
	
	/** @取得  坐席领取项目分机凭证 */
	public String getCertificate(){
		return certificate;
	}
	
	/** @设置  坐席领取项目分机凭证 */
	public void setCertificate(String certificate){
		this.certificate = certificate;
	}
	
	/** @取得  分配给该项目的分机号段 */
	public String getExtNumInterval(){
		return extNumInterval;
	}
	
	/** @设置  分配给该项目的分机号段 */
	public void setExtNumInterval(String extNumInterval){
		this.extNumInterval = extNumInterval;
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
	
	/** @取得  状态1进行中2完结 */
	public Integer getStatus(){
		return status;
	}
	
	/** @设置  状态1进行中2完结 */
	public void setStatus(Integer status){
		this.status = status;
	}

	public Long getFreeswitchId() {
		return freeswitchId;
	}

	public void setFreeswitchId(Long freeswitchId) {
		this.freeswitchId = freeswitchId;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getNativeServers() {
		return nativeServers;
	}

	public void setNativeServers(String nativeServers) {
		this.nativeServers = nativeServers;
	}
}
