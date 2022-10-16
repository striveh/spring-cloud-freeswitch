<?xml version="1.0" encoding="UTF-8" ?>  
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<#assign count=8><#-- 一行多少个字段 -->
<#assign selectListStr="">
<#assign valuesListStr="">
<#list columnList as col> 
<#assign selectListStr=(selectListStr + col.columnName + (columnList?size != col_index+1)?string(",", "") )>
<#assign selectListStr=(selectListStr + ((col_index+1) % count = 0)?string("\r\n				", ""))>
<#assign valuesListStr=(valuesListStr + "#{" +col.fieldName+"}"+ (columnList?size != col_index+1)?string(",", "") )>
<#assign valuesListStr=(valuesListStr + ((col_index+1) % count = 0)?string("\r\n				", ""))>
</#list> 
	
<!--【${tableName} ${tableComment}】sql -->
<mapper namespace="${fullPkgModel}.dao.${capClassName}Dao">
	<!-- ==============================非基础操作======================================= -->
	
	<!-- ==============================基础操作======================================= -->
	<!--根据条件得到列表 -->
	<select id="selectList" parameterType="${fullPkgDaoModel}.${capClassName}Pojo" resultType="${fullPkgDaoModel}.${capClassName}Pojo">
		<![CDATA[ 
			SELECT ${selectListStr}
			FROM ${tableName} 
		]]>
	</select>

	<!--根据id得到记录 -->
	<select id="selectUnique" parameterType="${fullPkgDaoModel}.${capClassName}Pojo" resultType="${fullPkgDaoModel}.${capClassName}Pojo">
		<![CDATA[ 
			SELECT ${selectListStr}
			FROM ${tableName} WHERE id = ${r"#{id}"}
		]]>
	</select>

	<!-- 新增记录 -->
	<insert id="insert" parameterType="${fullPkgDaoModel}.${capClassName}Pojo"
		useGeneratedKeys="true"	keyProperty="id" keyColumn="id">
		<![CDATA[ 
			INSERT INTO ${tableName} (
			    ${selectListStr}
			) VALUES (
				${valuesListStr}
			)
		]]>
	</insert>
	
	<!-- 批量新增记录 -->
	<insert id="insertList" parameterType="${fullPkgDaoModel}.${capClassName}Pojo">
		<![CDATA[ 
			INSERT INTO ${tableName} (
			    ${selectListStr}
			) VALUES (
				${valuesListStr}
			)
		]]>
	</insert>
	
	<!-- 修改记录 -->
	<update id="update" parameterType="${fullPkgDaoModel}.${capClassName}Pojo">
		<![CDATA[ UPDATE ${tableName}]]>
		<set>
		<#list columnList as col> 
	 		<if test="${col.fieldName} != null<#if col.fieldType = "String"> and ${col.fieldName} != ''</#if>"><![CDATA[ ${col.columnName} = ${"#{"}${col.fieldName}${"}"},]]></if>
		</#list> 
		</set>
		 <![CDATA[ WHERE id = ${r"#{id}"} ]]>
	</update>
	
	<!-- 删除记录 -->
	<update id="delete" parameterType="${fullPkgDaoModel}.${capClassName}Pojo">
		<![CDATA[ DELETE FROM ${tableName} where id = ${r"#{id}"} ]]>
	</update>
</mapper> 
