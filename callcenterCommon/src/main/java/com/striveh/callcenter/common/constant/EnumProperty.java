/** */
package com.striveh.callcenter.common.constant;

import com.striveh.callcenter.common.util.JsonTool;

public class EnumProperty {
	/** 状态值 */
	private int val;
	/** 状态编号 */
	private String code;
	/** 状态名称 */
	private String name;
	/** 显示名称 */
	private String dispName;

	/**
	 * 构造函数
	 * 
	 * @param val
	 * @param name
	 */
	public EnumProperty(int val, String name, String dispName) {
		super();
		this.val = val;
		this.name = name;
		this.dispName = dispName;
	}

	/**
	 * 构造函数
	 * 
	 * @param code
	 * @param name
	 */
	public EnumProperty(String code, String name, String dispName) {
		super();
		this.code = code;
		this.name = name;
		this.dispName = dispName;
	}

	/**
	 * 构造函数
	 * 
	 * @param val
	 * @param code
	 * @param name
	 */
	public EnumProperty(int val, String code, String name, String dispName) {
		super();
		this.val = val;
		this.code = code;
		this.name = name;
		this.dispName = dispName;
	}

	@Override
	public String toString() {
		return JsonTool.getString(this);
	}

	/** @取得 状态值 */
	public int getVal() {
		return val;
	}

	/** @设置 状态值 */
	public void setVal(int val) {
		this.val = val;
	}

	/** @取得 状态编号 */
	public String getCode() {
		return code;
	}

	/** @设置 状态编号 */
	public void setCode(String code) {
		this.code = code;
	}

	/** @取得 状态名称 */
	public String getName() {
		return name;
	}

	/** @设置 状态名称 */
	public void setName(String name) {
		this.name = name;
	}

	/** @取得 显示名称 */
	public String getDispName() {
		return dispName;
	}

	/** @设置 显示名称 */
	public void setDispName(String dispName) {
		this.dispName = dispName;
	}

}
