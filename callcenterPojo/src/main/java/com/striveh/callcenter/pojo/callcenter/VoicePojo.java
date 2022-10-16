/** */
package com.striveh.callcenter.pojo.callcenter;

import java.sql.Timestamp;
import com.striveh.callcenter.common.base.pojo.BasePojo;

/**
 * @功能:【voice 】PO
 * @项目名:callcenterPojo
 * @作者:xxx
 * @日期:2020-12-18 16:55:45
 * @说明：<pre></pre>
 */
public class VoicePojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
	/** 语音代码 */
	private String voiceCode;
	/** 文件地址 */
	private String patch;
	/**  */
	private Timestamp createTime;

	
	/** @取得  语音代码 */
	public String getVoiceCode(){
		return voiceCode;
	}
	
	/** @设置  语音代码 */
	public void setVoiceCode(String voiceCode){
		this.voiceCode = voiceCode;
	}
	
	/** @取得  文件地址 */
	public String getPatch(){
		return patch;
	}
	
	/** @设置  文件地址 */
	public void setPatch(String patch){
		this.patch = patch;
	}
	
	/** @取得   */
	public Timestamp getCreateTime(){
		return createTime;
	}
	
	/** @设置   */
	public void setCreateTime(Timestamp createTime){
		this.createTime = createTime;
	}

}
