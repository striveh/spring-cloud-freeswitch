package com.striveh.callcenter.freeswitch;

import com.alibaba.druid.util.StringUtils;
import com.striveh.callcenter.freeswitch.codemaker.po.CodeMakerCfg;
import com.striveh.callcenter.freeswitch.codemaker.service.AbsCodeMakerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * User: xxx
 * Date: 2018/4/12
 * Time: 17:56
 * Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CodeGenerate extends AbsCodeMakerService {

    /**
     * @设置 单条sql操作模板
     */
    @Autowired
    @Qualifier("masterDBSqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;



    /**
     *  生产 controller.service.dao.xml
     * @throws Exception
     */
    @Test
    public void codeGenerate() throws Exception {
        String dbName = "callcenter";
        String[] tableNames = {"voice"};
        String autherName = "xxx@striveh.com";
        String modlePackage = "com.striveh.callcenter.server.callcenter"; //生产的文件所在包名
        String projectName = "callcenterServer"; //在哪个项目生产文件
        String daoSourceModlePackage = "com.striveh.callcenter.pojo.callcenter"; //对应pojo所在包名
        String notGenerateFields = "id,"; //这些字段统一在basePojo中

        String[] notGenerateTemplates = {"Pojo.java"};
        for (String tableName : tableNames) {
            if (!StringUtils.isEmpty(tableName)) {
                CodeMakerCfg cfg = new CodeMakerCfg();
                cfg.setDb(dbName);
                cfg.setTableName(tableName);
                cfg.setAutherName(autherName);
                cfg.setModlePackage(modlePackage);
                cfg.setProjectName(projectName);
                cfg.setDaoSourceModlePackage(daoSourceModlePackage);
                cfg.setNotGenerateFields(notGenerateFields);
                cfg.setNotGenerateTemplates(notGenerateTemplates);
                this.generateCode(cfg, sqlSessionTemplate);
            }
        }
    }

    /**
     *  生产 pojo
     * @throws Exception
     */
    @Test
    public void codeGeneratePojo() throws Exception {
        String dbName = "callcenter";//数据库名称
        String[] tableNames = {"voice"};
        String autherName = "xxx";//作者
        String modlePackage = "com.striveh.callcenter.pojo.callcenter"; //生产的文件所在包名
        String projectName = "callcenterPojo"; //在哪个项目生产文件
        String daoSourceModlePackage = "com.striveh.callcenter.pojo.callcenter";//对应pojo所在包名
        String notGenerateFields = "id,";

        String[] notGenerateTemplates = {"Controller.java","Service.java","Dao.java",".xml","Test.java"};
        for (String tableName : tableNames) {
            if (!StringUtils.isEmpty(tableName)) {
                CodeMakerCfg cfg = new CodeMakerCfg();
                cfg.setDb(dbName);
                cfg.setTableName(tableName);
                cfg.setAutherName(autherName);
                cfg.setModlePackage(modlePackage);
                cfg.setProjectName(projectName);
                cfg.setDaoSourceModlePackage(daoSourceModlePackage);
                cfg.setNotGenerateFields(notGenerateFields);
                cfg.setNotGenerateTemplates(notGenerateTemplates);
                this.generateCode(cfg, sqlSessionTemplate);
            }
        }
    }
}
