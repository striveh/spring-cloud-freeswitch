/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【tasks 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:03
 * @说明：<pre></pre>
 */
public class TasksPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/**  */
	private Integer taskId;
	/**  */
	private String taskDesc;
	/**  */
	private String taskGroup;
	/**  */
	private Long taskRuntime;
	/**  */
	private Integer taskSqlManager;
	/**  */
	private String hostname;

	
	/** @取得   */
	public Integer getTaskId(){
		return taskId;
	}
	
	/** @设置   */
	public void setTaskId(Integer taskId){
		this.taskId = taskId;
	}
	
	/** @取得   */
	public String getTaskDesc(){
		return taskDesc;
	}
	
	/** @设置   */
	public void setTaskDesc(String taskDesc){
		this.taskDesc = taskDesc;
	}
	
	/** @取得   */
	public String getTaskGroup(){
		return taskGroup;
	}
	
	/** @设置   */
	public void setTaskGroup(String taskGroup){
		this.taskGroup = taskGroup;
	}
	
	/** @取得   */
	public Long getTaskRuntime(){
		return taskRuntime;
	}
	
	/** @设置   */
	public void setTaskRuntime(Long taskRuntime){
		this.taskRuntime = taskRuntime;
	}
	
	/** @取得   */
	public Integer getTaskSqlManager(){
		return taskSqlManager;
	}
	
	/** @设置   */
	public void setTaskSqlManager(Integer taskSqlManager){
		this.taskSqlManager = taskSqlManager;
	}
	
	/** @取得   */
	public String getHostname(){
		return hostname;
	}
	
	/** @设置   */
	public void setHostname(String hostname){
		this.hostname = hostname;
	}

}
