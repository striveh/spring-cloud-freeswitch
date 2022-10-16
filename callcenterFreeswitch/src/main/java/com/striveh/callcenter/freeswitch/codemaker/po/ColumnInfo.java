/** */
package com.striveh.callcenter.freeswitch.codemaker.po;

import com.striveh.callcenter.common.base.pojo.BasePojo;

public class ColumnInfo extends BasePojo {
	/** */
	private static final long serialVersionUID = 1L;
	/** 数据库 */
	public String db;
	/** 表名 */
	public String tableName;
	/** 表注释 */
	public String tableComment;
	/** 列名 */
	public String columnName;
	/** 数据类型 */
	public String dataType;
	/** 长度 */
	public int colLength;
	/** 精度 */
	public int scale;
	/** 列注释 */
	public String columnComment;

	/** java类名，首字母小写 */
	public String className;
	/** java类名，首字母大写 */
	public String capClassName;
	/** java属性数据类型 */
	public String fieldType;
	/** java属性名 ，首字母小写 */
	public String fieldName;
	/** java属性名 ，首字母大写 */
	public String capFieldName;
	/** 产生类的属性 */
	public boolean ableMakerField = true;

	/**
	 * @取得 数据库
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @设置 数据库
	 */
	public void setDb(String db) {
		this.db = db;
	}

	/**
	 * @取得 表名
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @设置 表名
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @取得 表注释
	 */
	public String getTableComment() {
		return tableComment;
	}

	/**
	 * @设置 表注释
	 */
	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}

	/**
	 * @取得 列名
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @设置 列名
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @取得 数据类型
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @设置 数据类型
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @取得 长度
	 */
	public int getColLength() {
		return colLength;
	}

	/**
	 * @设置 长度
	 */
	public void setColLength(int colLength) {
		this.colLength = colLength;
	}

	/**
	 * @取得 精度
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @设置 精度
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @取得 列注释
	 */
	public String getColumnComment() {
		return columnComment;
	}

	/**
	 * @设置 列注释
	 */
	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}

	/**
	 * @取得 java类名，首字母小写
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @设置 java类名，首字母小写
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @取得 java类名，首字母大写
	 */
	public String getCapClassName() {
		return capClassName;
	}

	/**
	 * @设置 java类名，首字母大写
	 */
	public void setCapClassName(String capClassName) {
		this.capClassName = capClassName;
	}

	/**
	 * @取得 java属性数据类型
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @设置 java属性数据类型
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @取得 java属性名，首字母小写
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @设置 java属性名，首字母小写
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @取得 java属性名，首字母大写
	 */
	public String getCapFieldName() {
		return capFieldName;
	}

	/**
	 * @设置 java属性名，首字母大写
	 */
	public void setCapFieldName(String capFieldName) {
		this.capFieldName = capFieldName;
	}

	/**
	 * @取得 产生类的属性
	 */
	public boolean isAbleMakerField() {
		return ableMakerField;
	}

	/**
	 * @设置 产生类的属性
	 */
	public void setAbleMakerField(boolean ableMakerField) {
		this.ableMakerField = ableMakerField;
	}

}
