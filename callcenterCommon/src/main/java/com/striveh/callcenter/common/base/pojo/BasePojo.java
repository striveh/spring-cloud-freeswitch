/** */
package com.striveh.callcenter.common.base.pojo;


import com.striveh.callcenter.common.util.JsonTool;

import java.io.Serializable;

public class BasePojo implements Serializable {
	/** */
	private static final long serialVersionUID = 1L;
	/** id */
	private Long id;

	/** 分页：第几页 */
	private Integer pageNum;
	/** 分页：每页显示条数 */
	private Integer pageSize;
	/** 分页：查询记录总数 */
	private Long totalRowCount;

	/**
	 * @取得 id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @设置 id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @取得 分页：第几页
	 */
	public Integer getPageNum() {
		return pageNum;
	}

	/**
	 * @设置 分页：第几页
	 */
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * @取得 分页：每页显示条数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @设置 分页：每页显示条数
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @取得 分页：查询记录总数
	 */
	public Long getTotalRowCount() {
		return totalRowCount;
	}

	/**
	 * @设置 分页：查询记录总数
	 */
	public void setTotalRowCount(Long totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * @方法重写
	 */
	@Override
	public String toString() {
		return JsonTool.getString(this);
	}

}
