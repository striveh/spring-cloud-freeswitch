/** */
package com.striveh.callcenter.pojo.sys;

import com.striveh.callcenter.common.base.pojo.BasePojo;

import java.sql.Timestamp;

public class SysAuthInfoPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 账号 */
	private String account;
	/** 密钥 */
	private String secret;
	/** 账户类型：1mgmt接口 */
	private Integer type;
	/** 使用者 */
	private String user;
	/** 备注 */
	private String remark;
	/** 过期时间 */
	private Timestamp expirTime;
	/** 状态(0停用、1正常) */
	private Integer status;
	/** 创建人id */
	private Long createrId;
	/** 创建人姓名 */
	private String createrName;
	/** 创建时间 */
	private Timestamp createTime;
	/** 修改人id */
	private Long modId;
	/** 修改人姓名 */
	private String modName;
	/** 修改时间 */
	private Timestamp modTime;
	
	/** @取得  账号 */
	public String getAccount(){
		return account;
	}
	
	/** @设置  账号 */
	public void setAccount(String account){
		this.account = account;
	}
	
	/** @取得  密钥 */
	public String getSecret(){
		return secret;
	}
	
	/** @设置  密钥 */
	public void setSecret(String secret){
		this.secret = secret;
	}
	
	/** @取得  账户类型：1mgmt接口 */
	public Integer getType(){
		return type;
	}
	
	/** @设置  账户类型：1mgmt接口 */
	public void setType(Integer type){
		this.type = type;
	}
	
	/** @取得  使用者 */
	public String getUser(){
		return user;
	}
	
	/** @设置  使用者 */
	public void setUser(String user){
		this.user = user;
	}
	
	/** @取得  备注 */
	public String getRemark(){
		return remark;
	}
	
	/** @设置  备注 */
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	/** @取得  过期时间 */
	public Timestamp getExpirTime(){
		return expirTime;
	}
	
	/** @设置  过期时间 */
	public void setExpirTime(Timestamp expirTime){
		this.expirTime = expirTime;
	}
	
	/** @取得  状态(0停用、1正常) */
	public Integer getStatus(){
		return status;
	}
	
	/** @设置  状态(0停用、1正常) */
	public void setStatus(Integer status){
		this.status = status;
	}

	/**
	 * @取得 创建人id
	 */
	public Long getCreaterId() {
		return createrId;
	}

	/**
	 * @设置 创建人id
	 */
	public void setCreaterId(Long createrId) {
		this.createrId = createrId;
	}

	/**
	 * @取得 创建人姓名
	 */
	public String getCreaterName() {
		return createrName;
	}

	/**
	 * @设置 创建人姓名
	 */
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	/**
	 * @取得 创建时间
	 */
	public Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @设置 创建时间
	 */
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @取得 修改人id
	 */
	public Long getModId() {
		return modId;
	}

	/**
	 * @设置 修改人id
	 */
	public void setModId(Long modId) {
		this.modId = modId;
	}

	/**
	 * @取得 修改人姓名
	 */
	public String getModName() {
		return modName;
	}

	/**
	 * @设置 修改人姓名
	 */
	public void setModName(String modName) {
		this.modName = modName;
	}

	/**
	 * @取得 修改时间
	 */
	public Timestamp getModTime() {
		return modTime;
	}

	/**
	 * @设置 修改时间
	 */
	public void setModTime(Timestamp modTime) {
		this.modTime = modTime;
	}

}
