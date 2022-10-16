/** */
package com.striveh.callcenter.pojo.freeswitch;

import com.striveh.callcenter.common.base.pojo.BasePojo;

import java.sql.Timestamp;

/**
 * @功能:【gateway 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-04-06 10:35:01
 * @说明：<pre></pre>
 */
public class GatewayPojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 线路编号 */
	private String gwCode;
	/** 线路名称 */
	private String gwName;
	/** 最大并发量 */
	private Integer concurrency;
	/** 已用并发量 */
	private Integer usedConcurrency;
	/** 可用并发量 */
	private Integer availableConcurrency;
	/** 备注描述 */
	private String remark;
	/** 网关参数username */
	private String username;
	/** 网关参数password */
	private String password;
	/** 网关参数realm */
	private String realm;
	/** 网关参数register */
	private String register;
	/**网关注册类型1主动模式2被动模式*/
	private Integer registerType;
	/** 呼叫次数 */
	private Integer callLimit;
	/** 计费单价 */
	private Double price;
	/** 计费方式 1按次数、2按秒(每多少秒多少钱) */
	private Integer billType;
	/** 按秒(每多少秒多少钱)秒数 */
	private Integer billsec;
	/** 主叫号码 */
	private String originationCallId;
	/** 语音编码 */
	private String codec;
	/** 呼叫手机前缀 */
	private String mobilePhonePrefix;
	/** 呼叫座机前缀 */
	private String fixedPhonePrefix;
	/** 状态 0禁用、1启用 */
	private Integer status;
	/** 预计线路数 */
	private Integer expectCount;
	/** 添加时间 */
	private Timestamp addTime;
	/** 更新时间 */
	private Timestamp updateTime;

	/** 回访线路: 0否、1是 */
	private Integer returnVisit;

	/** 账户余额 */
	private Double accountBalance;

	/** 已充值金额 */
	private Double rechargedAmount;

	
	/** @取得  线路编号 */
	public String getGwCode(){
		return gwCode;
	}
	
	/** @设置  线路编号 */
	public void setGwCode(String gwCode){
		this.gwCode = gwCode;
	}
	
	/** @取得  线路名称 */
	public String getGwName(){
		return gwName;
	}
	
	/** @设置  线路名称 */
	public void setGwName(String gwName){
		this.gwName = gwName;
	}
	
	/** @取得  最大并发量 */
	public Integer getConcurrency(){
		return concurrency;
	}
	
	/** @设置  最大并发量 */
	public void setConcurrency(Integer concurrency){
		this.concurrency = concurrency;
	}
	
	/** @取得  已用并发量 */
	public Integer getUsedConcurrency(){
		return usedConcurrency;
	}
	
	/** @设置  已用并发量 */
	public void setUsedConcurrency(Integer usedConcurrency){
		this.usedConcurrency = usedConcurrency;
	}
	
	/** @取得  可用并发量 */
	public Integer getAvailableConcurrency(){
		return availableConcurrency;
	}
	
	/** @设置  可用并发量 */
	public void setAvailableConcurrency(Integer availableConcurrency){
		this.availableConcurrency = availableConcurrency;
	}
	
	/** @取得  备注描述 */
	public String getRemark(){
		return remark;
	}
	
	/** @设置  备注描述 */
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	/** @取得  网关参数username */
	public String getUsername(){
		return username;
	}
	
	/** @设置  网关参数username */
	public void setUsername(String username){
		this.username = username;
	}
	
	/** @取得  网关参数password */
	public String getPassword(){
		return password;
	}
	
	/** @设置  网关参数password */
	public void setPassword(String password){
		this.password = password;
	}
	
	/** @取得  网关参数realm */
	public String getRealm(){
		return realm;
	}
	
	/** @设置  网关参数realm */
	public void setRealm(String realm){
		this.realm = realm;
	}
	
	/** @取得  网关参数register */
	public String getRegister(){
		return register;
	}
	
	/** @设置  网关参数register */
	public void setRegister(String register){
		this.register = register;
	}

	public Integer getRegisterType() {
		return registerType;
	}

	public void setRegisterType(Integer registerType) {
		this.registerType = registerType;
	}

	public Integer getCallLimit() {
		return callLimit;
	}

	public void setCallLimit(Integer callLimit) {
		this.callLimit = callLimit;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getBillType() {
		return billType;
	}

	public void setBillType(Integer billType) {
		this.billType = billType;
	}

	public Integer getBillsec() {
		return billsec;
	}

	public void setBillsec(Integer billsec) {
		this.billsec = billsec;
	}

	public String getOriginationCallId() {
		return originationCallId;
	}

	public void setOriginationCallId(String originationCallId) {
		this.originationCallId = originationCallId;
	}

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public String getMobilePhonePrefix() {
		return mobilePhonePrefix;
	}

	public void setMobilePhonePrefix(String mobilePhonePrefix) {
		this.mobilePhonePrefix = mobilePhonePrefix;
	}

	public String getFixedPhonePrefix() {
		return fixedPhonePrefix;
	}

	public void setFixedPhonePrefix(String fixedPhonePrefix) {
		this.fixedPhonePrefix = fixedPhonePrefix;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getExpectCount() {
		return expectCount;
	}

	public void setExpectCount(Integer expectCount) {
		this.expectCount = expectCount;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getReturnVisit() {
		return returnVisit;
	}

	public void setReturnVisit(Integer returnVisit) {
		this.returnVisit = returnVisit;
	}

	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Double getRechargedAmount() {
		return rechargedAmount;
	}

	public void setRechargedAmount(Double rechargedAmount) {
		this.rechargedAmount = rechargedAmount;
	}

}
