/** */
package com.striveh.callcenter.pojo.callcenter;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【freeswitch freeswitch服务实例表】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-05-17 16:53:21
 * @说明：<pre></pre>
 */
public class FreeswitchPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 服务实例主机地址 */
	private String host;
	/** 服务实例主机ESL端口 */
	private Integer port;
	/** 服务实例主机ESL密码 */
	private String password;
	/** 支持的最大通话并发数 */
	private Integer callConcurrent;
	/** 状态0停用1启用 */
	private Integer status;
	/** 添加时间 */
	private Timestamp addTime;
	/** 更新时间 */
	private Timestamp updateTime;
	/** 服务实例用于native软电话使用的服务地址 */
	private String nativeSipPhone;
	/** 服务实例用于web软电话使用的服务地址 */
	private String webSipPhone;
	/** 服务能力权重 */
	private Integer weight;

	public FreeswitchPojo(Long id) {
		this.setId(id);
	}

	public FreeswitchPojo() {

	}
	
	/** @取得  服务实例主机地址 */
	public String getHost(){
		return host;
	}
	
	/** @设置  服务实例主机地址 */
	public void setHost(String host){
		this.host = host;
	}
	
	/** @取得  服务实例主机ESL端口 */
	public Integer getPort(){
		return port;
	}
	
	/** @设置  服务实例主机ESL端口 */
	public void setPort(Integer port){
		this.port = port;
	}
	
	/** @取得  服务实例主机ESL密码 */
	public String getPassword(){
		return password;
	}
	
	/** @设置  服务实例主机ESL密码 */
	public void setPassword(String password){
		this.password = password;
	}
	
	/** @取得  支持的最大通话并发数 */
	public Integer getCallConcurrent(){
		return callConcurrent;
	}
	
	/** @设置  支持的最大通话并发数 */
	public void setCallConcurrent(Integer callConcurrent){
		this.callConcurrent = callConcurrent;
	}
	
	/** @取得  状态0停用1启用 */
	public Integer getStatus(){
		return status;
	}
	
	/** @设置  状态0停用1启用 */
	public void setStatus(Integer status){
		this.status = status;
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

	public String getNativeSipPhone() {
		return nativeSipPhone;
	}

	public void setNativeSipPhone(String nativeSipPhone) {
		this.nativeSipPhone = nativeSipPhone;
	}

	public String getWebSipPhone() {
		return webSipPhone;
	}

	public void setWebSipPhone(String webSipPhone) {
		this.webSipPhone = webSipPhone;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
