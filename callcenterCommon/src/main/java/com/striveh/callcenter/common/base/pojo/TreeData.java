package com.striveh.callcenter.common.base.pojo;

import java.io.Serializable;
import java.util.List;
import com.striveh.callcenter.common.util.JsonTool;

public class TreeData implements Serializable {
	/** */
	private static final long serialVersionUID = 8393433166302520470L;
	/** 树节点id */
	private Long id;
	/** 树节点code */
	private String code;
	/** 树节点名称 */
	private String name;
	/** 树节点是否有效 */
	private Boolean invalid;
	/** 上级id */
	private Long parentId;
	/** 图标 */
	private String icon;
	/** url链接 */
	private String url;
	/** 层级 */
	private Integer level;
	/** 同级排序 */
	private Integer orderCode;
	/** 子节点信息 */
	private List<TreeData> children;
	/** 是否选中 */
	private Boolean checked;

	/** @取得 树节点id */
	public Long getId() {
		return id;
	}

	/** @设置 树节点id */
	public void setId(Long id) {
		this.id = id;
	}

	/** @取得 树节点code */
	public String getCode() {
		return code;
	}

	/** @设置 树节点code */
	public void setCode(String code) {
		this.code = code;
	}

	/** @取得 树节点名称 */
	public String getName() {
		return name;
	}

	/** @设置 树节点名称 */
	public void setName(String name) {
		this.name = name;
	}

	/** @取得 树节点是否有效 */
	public Boolean getInvalid() {
		return invalid;
	}

	/** @设置 树节点是否有效 */
	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}

	/** @取得 上级id */
	public Long getParentId() {
		return parentId;
	}

	/** @设置 上级id */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/** @取得 图标 */
	public String getIcon() {
		return icon;
	}

	/** @设置 图标 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/** @取得 url链接 */
	public String getUrl() {
		return url;
	}

	/** @设置 url链接 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** @取得 层级 */
	public Integer getLevel() {
		return level;
	}

	/** @设置 层级 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/** @取得 同级排序 */
	public Integer getOrderCode() {
		return orderCode;
	}

	/** @设置 同级排序 */
	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}

	/** @取得 子节点信息 */
	public List<TreeData> getChildren() {
		return children;
	}

	/** @设置 子节点信息 */
	public void setChildren(List<TreeData> children) {
		this.children = children;
	}

	/** 获取 是否选中 */
	public Boolean getChecked() {
		return this.checked;
	}

	/** 设置 是否选中 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * @方法重写
	 */
	@Override
	public String toString() {
		return JsonTool.getString(this);
	}
}
