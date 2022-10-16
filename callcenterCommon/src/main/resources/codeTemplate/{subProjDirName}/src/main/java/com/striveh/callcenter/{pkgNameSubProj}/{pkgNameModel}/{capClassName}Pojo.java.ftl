/** */
package ${fullPkgModel};

<#if useDate = true >
import java.sql.Date;
</#if>
<#if useTimestamp = true >
import java.sql.Timestamp;
</#if>
import ${fullPkgProject}.common.base.pojo.BasePojo;

/**
 * @功能:【${tableName} ${tableComment}】PO
 * @项目名:${projectDirName}
 * @作者:${autherName}
 * @日期:${createTime?string('yyyy-MM-dd HH:mm:ss')}
 * @说明：<pre></pre>
 */
public class ${capClassName}Pojo extends BasePojo {
	/** 序列化UID */
	private static final long serialVersionUID = 1L;
<#list columnList as col> 
	<#if col.ableMakerField = true >
	/** ${col.columnComment} */
	private ${col.fieldType} ${col.fieldName};
	</#if>
</#list> 

<#list columnList as col> 
	<#if col.ableMakerField = true >
	
	/** @取得  ${col.columnComment} */
	public ${col.fieldType} get${col.capFieldName}(){
		return ${col.fieldName};
	}
	
	/** @设置  ${col.columnComment} */
	public void set${col.capFieldName}(${col.fieldType} ${col.fieldName}){
		this.${col.fieldName} = ${col.fieldName};
	}
	</#if>
</#list> 

}
