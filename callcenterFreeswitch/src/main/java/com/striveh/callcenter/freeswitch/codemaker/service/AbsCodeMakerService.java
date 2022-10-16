/** */
package com.striveh.callcenter.freeswitch.codemaker.service;

import com.github.pagehelper.util.StringUtil;
import com.striveh.callcenter.freeswitch.codemaker.po.CodeMakerCfg;
import com.striveh.callcenter.freeswitch.codemaker.po.ColumnInfo;
import com.striveh.callcenter.freeswitch.codemaker.dao.CodeMakerDao;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.Map.Entry;

public abstract class AbsCodeMakerService {
	/** 日志 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	public static Map<String, String> dataTypeConverCfg = new HashMap<String, String>();

	/**
	 * 初始化，并产生代码
	 * 
	 * @param cfg
	 * @param sqlSessionTemplate
	 * @throws Exception
	 */
	public void generateCode(CodeMakerCfg cfg, SqlSessionTemplate sqlSessionTemplate) throws Exception {
		/** 取得列信息，设置类名及java数据类型 */
		ColumnInfo queryParam = new ColumnInfo();
		queryParam.setDb(cfg.getDb());
		queryParam.setTableName(cfg.getTableName());

		CodeMakerDao dao = new CodeMakerDao();
		dao.setTemplate2(sqlSessionTemplate);
		List<ColumnInfo> columnList = dao.selectList(queryParam);
		if (columnList == null || columnList.size() == 0) {
			logger.info("没有找到数据库表信息");
			return;
		}

		/** 生成参数信息,取得模板目录、项目根目录 */
		Map<String, Object> dataMap = cfg.getCustomParam();
		if (dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}
		File projectDir = getDataMap(dataMap, cfg, columnList);

		// 取得模板目录、项目根目录
		String templateBasePath = CodeMakerCfg.class.getClass().getResource(cfg.getTemplateBasePath()).getFile();
		templateBasePath = tidyFileUrl(templateBasePath, null);
		File templateBaseDir = new File(templateBasePath);
		Set<String> templateNames = new HashSet<String>();
		getTemplatesByBaseDir(templateBaseDir, templateNames, templateBaseDir);// 找到目录下的所有模板

		/** 生成模板 */
		boolean isOk = makerCode(templateBaseDir, templateNames, cfg, projectDir, dataMap);
		if (isOk) {
			logger.info("文件生成成功");
		}
	}

	/**
	 * 生成代码
	 * 
	 * @param templateBaseDir
	 * @param templateNames
	 * @param cfg
	 * @param projectDir
	 * @param dataMap
	 * @return
	 * @throws Exception
	 */
	private boolean makerCode(File templateBaseDir, Set<String> templateNames, CodeMakerCfg cfg, File projectDir,
			Map<String, Object> dataMap) throws Exception {
		// 初始化模板
		Configuration markerCfg = new Configuration(Configuration.VERSION_2_3_25);
		markerCfg.setDirectoryForTemplateLoading(templateBaseDir);

		// 生成代码
		String classFileName = null;
		File classFile = null;
		Template template = null;
		for (String tpName : templateNames) {
			boolean isSkip = false;
			for (String notGenerate : cfg.getNotGenerateTemplates()) {
				if (tpName.indexOf(notGenerate) > 0) {
					logger.info("不产生指定的模板：{}", tpName);
					isSkip = true;
					break;
				}
			}
			if (isSkip) {
				continue;
			}

			classFileName = tidyFileUrl(tpName, dataMap);
			classFile = new File(projectDir, classFileName);
			if (classFile.exists()) {
				logger.info("文件已存在，跳过生成：{}", classFile.getPath());
				continue;
			} else {
				File fileParent = classFile.getParentFile();
				if (!fileParent.exists()) {
					fileParent.mkdirs();
				}
			}

			FileOutputStream fos = new FileOutputStream(classFile);
			OutputStreamWriter streamWriter = new OutputStreamWriter(fos, cfg.getFileEncoding());
			try {
				template = markerCfg.getTemplate(tpName);
				template.process(dataMap, streamWriter);
				streamWriter.flush();
			} catch (Exception ex) {
				classFile.delete();
				logger.info("文件生成失败", ex);
				return false;
			} finally {
				streamWriter.close();
				fos.close();
			}
		}
		return true;
	}

	/**
	 * 生成模板需要的参数
	 * 
	 * @param dataMap
	 * @param cfg
	 * @param columnList
	 * @retrun 项目根文件夹
	 */
	private File getDataMap(Map<String, Object> dataMap, CodeMakerCfg cfg, List<ColumnInfo> columnList) {
		dataMap.put("autherName", cfg.getAutherName());
		dataMap.put("createTime", cfg.getCreateTime() == null ? new Date() : cfg.getCreateTime());
		dataMap.put("useTimestamp", false);
		dataMap.put("useDate", false);
		for (ColumnInfo colInfo : columnList) {
			setJavaClassInfo(colInfo, cfg, dataMap);
		}
		ColumnInfo columnInfo = columnList.get(0);
		dataMap.put("db", columnInfo.getDb());
		dataMap.put("tableName", columnInfo.getTableName());
		dataMap.put("tableComment", columnInfo.getTableComment());
		dataMap.put("className", columnInfo.getClassName());
		dataMap.put("capClassName", columnInfo.getCapClassName());
		dataMap.put("columnList", columnList);

		// 设置要生成的项目的路径
		File subProjectDir = null;
		if(cfg.getProjectName().contains(":") || cfg.getProjectName().contains("/") || cfg.getProjectName().contains("\\\\")) {
			subProjectDir = new File(cfg.getProjectName());
		} else {
			String currentProjectPath = System.getProperty("user.dir");
			currentProjectPath = currentProjectPath.replaceAll("\\\\", "/");
			String project = currentProjectPath.substring(0, currentProjectPath.lastIndexOf("/") + 1) + cfg.getProjectName();
			subProjectDir = new File(project);
		}
		
		File projectDir = subProjectDir.getParentFile();
		dataMap.put("projectDirName", subProjectDir.getName());
		dataMap.put("subProjDirName", subProjectDir.getName());
		dataMap.put("pkgNameType", cfg.getPkgNameType());
		dataMap.put("pkgNameCompany", cfg.getPkgNameCompany());
		dataMap.put("pkgNameProject", cfg.getPkgNameProject());
		dataMap.put("pkgNameSubProj", cfg.getPkgNameSubProj());
		dataMap.put("pkgNameModel", cfg.getPkgNameModel());

		// 项目完整包名
		String fullPkg = cfg.getPkgNameType();
		if (!StringUtil.isEmpty(cfg.getPkgNameCompany())) {
			fullPkg += "." + cfg.getPkgNameCompany();
		}
		if (!StringUtil.isEmpty(cfg.getPkgNameProject())) {
			fullPkg += "." + cfg.getPkgNameProject();
		}
		dataMap.put("fullPkgProject", fullPkg);

		// 模块完整包名
		if (!StringUtil.isEmpty(cfg.getPkgNameSubProj())) {
			fullPkg += "." + cfg.getPkgNameSubProj();
		}
		dataMap.put("fullPkgSubProj", fullPkg);
		if (!StringUtil.isEmpty(cfg.getPkgNameModel())) {
			fullPkg += "." + cfg.getPkgNameModel();
		}
		dataMap.put("fullPkgModel", fullPkg);
		
		// 增加dao,pojo,sqlmap来源的fullPkgModel
		if(!StringUtil.isEmpty(cfg.getDaoSourceModlePackage())) {
			dataMap.put("fullPkgDaoModel", cfg.getDaoSourceModlePackage());
		}
		return projectDir;
	}
	
	/**
	 * 整理url，将url中参数替换，并将“\”替换成“/”
	 * 
	 * @param url
	 * @param dataMap 可以为空
	 * @return
	 */
	private String tidyFileUrl(String url, Map<String, Object> dataMap) {
		// templateBasePath=/E:/1_myProject/5_git/zfLendingPlatform/dunningCommon/target/classes/codemakertemplate
		if (url.startsWith("/") && url.indexOf(":") > 0) {
			url = url.substring(1);// 替换盘符前的“/”
		}
		if (dataMap != null) {
			for (Entry<String, Object> entry : dataMap.entrySet()) {
				url = url.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().toString());
			}
			url = url.replaceAll("\\.ftl", "");
		}
		url = url.replaceAll("\\\\", "/");
		url = url.replaceAll("//", "/");
		return url;
	}

	/**
	 * 递归找到baseDir目录下的所有模板
	 * 
	 * @param baseDir
	 * @param templateNames
	 * @param currDir
	 */
	private void getTemplatesByBaseDir(File baseDir, Set<String> templateNames, File currDir) {
		if (currDir.isDirectory()) {
			for (File tempDir : currDir.listFiles()) {
				getTemplatesByBaseDir(baseDir, templateNames, tempDir);
			}
		} else {
			String tpName = currDir.getPath().substring(baseDir.getPath().length());
			templateNames.add(tidyFileUrl(tpName, null));
		}
	}

	/**
	 * 设置类名及java数据类型
	 * 
	 * @param colInfo
	 */
	private void setJavaClassInfo(ColumnInfo colInfo, CodeMakerCfg cfg, Map<String, Object> dataMap) {
		String className = formatName(colInfo.getTableName());
		colInfo.setClassName(StringUtils.uncapitalize(className));
		colInfo.setCapClassName(StringUtils.capitalize(className));

		String fieldName = formatName(colInfo.getColumnName());
		colInfo.setFieldName(StringUtils.uncapitalize(fieldName));
		colInfo.setCapFieldName(StringUtils.capitalize(fieldName));
		String fieldType = dataTypeConverCfg.getOrDefault(colInfo.getDataType().toLowerCase(), "");
		colInfo.setFieldType(fieldType);

		if (cfg.getNotGenerateFields().contains(colInfo.getColumnName())) {
			colInfo.setAbleMakerField(false);// 此字段不生成java属性
		} else {
			// 是否用到Timestamp、Date，生成模板时是否导入class
			if ("Timestamp".equals(fieldType)) {
				dataMap.put("useTimestamp", true);
			}
			if ("Date".equals(fieldType)) {
				dataMap.put("useDate", true);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		String project = "jumiPersistence";
		String currentProjectPath = System.getProperty("user.dir");
		currentProjectPath = currentProjectPath.replaceAll("\\\\", "/");
		project = currentProjectPath.substring(0, currentProjectPath.lastIndexOf("/") + 1) + "jumiPersistence";
		File subProjectDir = new File(currentProjectPath);
		File projectDir = subProjectDir.getParentFile();
		System.out.println(subProjectDir.getName());
		System.out.println(projectDir.getName());
		System.out.println(project);
	}
	

	/**
	 * 名字转成驼峰命名
	 * 
	 * @param name
	 * @return
	 */
	private String formatName(String name) {
		if (name.contains("_")) {
			String[] ns = name.split("_");
			StringBuffer buffer = new StringBuffer();
			for (String n : ns) {
				buffer.append(StringUtils.capitalize(n));
			}
			return buffer.toString();
		}
		return name;
	}

	static {
		// 数据库类型,java类型
		dataTypeConverCfg.put("bigint", "Long");
		dataTypeConverCfg.put("binary", "Integer");
		dataTypeConverCfg.put("date", "Date");
		dataTypeConverCfg.put("datetime", "Timestamp");
		dataTypeConverCfg.put("decimal", "Double");
		dataTypeConverCfg.put("double", "Double");
		dataTypeConverCfg.put("float", "Float");
		dataTypeConverCfg.put("tinyint", "Integer");
		dataTypeConverCfg.put("smallint", "Integer");
		dataTypeConverCfg.put("mediumint", "Integer");
		dataTypeConverCfg.put("int", "Integer");
		dataTypeConverCfg.put("integer", "Integer");
		dataTypeConverCfg.put("text", "String");
		dataTypeConverCfg.put("longtext", "String");
		dataTypeConverCfg.put("numeric", "Double");
		dataTypeConverCfg.put("timestamp", "Timestamp");
		dataTypeConverCfg.put("varchar", "String");
		dataTypeConverCfg.put("varchar2", "String");
		dataTypeConverCfg.put("nvarchar", "String");
		dataTypeConverCfg.put("nvarchar2", "String");
	}
}
