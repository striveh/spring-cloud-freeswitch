/** */
package com.striveh.callcenter.server.callcenter.service;

import com.striveh.callcenter.common.util.HttpTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.IFreeswitchService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.server.callcenter.dao.CallProjectDao;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @功能:【callProject 呼叫项目表】Service
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
@Service
public class CallProjectService extends BaseService<CallProjectPojo, CallProjectDao> implements ICallProjectService {

    @Autowired
    private InboundClient inboundClient;

    @Value("${common.fifo.filepath}")
    private String fifoFilePath;
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private IFreeswitchService freeswitchService;

    @Async
    @Override
    public void callBack(Map<String, String> params,String url,String log) {
        Long timestamp=System.currentTimeMillis();
        params.put("sign", StringTool.MD5Encode(timestamp+"TheCALLV1"));
        params.put("timestamp", String.valueOf(timestamp));
        logger.info(log+"请求参数{}", JsonTool.getJsonString(params));
        try {
            String result = HttpTool.requestPost(null,url,params);
            logger.info(log+"请求结果{}",result);
        } catch (Exception ex) {
            logger.error(log+"异常",ex);
        }
    }

    @Override
    public void createProject(CallProjectPojo callProjectPojo) {
        insert(callProjectPojo);
        configFifo();
    }

    @Override
    public void endProject(CallProjectPojo callProjectPojo) {
        update(callProjectPojo);
        configFifo();
    }

    private synchronized void configFifo() {
        File file = new File(fifoFilePath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file,false);
            fileWriter.append("<configuration name=\"fifo.conf\" description=\"FIFO Configuration\">"+"\n");
            fileWriter.append("  <fifos>"+"\n");

            CallProjectPojo callProjectPojo=new CallProjectPojo();
            callProjectPojo.setStatus(1);
            List<CallProjectPojo> callProjectPojos= this.selectList(callProjectPojo);
            for (CallProjectPojo e : callProjectPojos){
                fileWriter.append("    <fifo name=\""+e.getProjectCode()+"\" importance=\"0\"></fifo>"+"\n");
            }

            fileWriter.append("  </fifos>"+"\n");
            fileWriter.append("</configuration>");
            fileWriter.flush();

            inboundClient.option().serverOptions().forEach(e->{
                EslMessage eslMessageReloadxml=inboundClient.sendSyncApiCommand(e.addr(), "reloadxml",null);
                EslMessage eslMessage=inboundClient.sendSyncApiCommand(e.addr(), "fifo reparse",null);
                logger.info("FS{}更新队列结果{}",e.addr() , EslHelper.formatEslMessage(eslMessage));
            });

        } catch (IOException e) {
            logger.error("配置队列异常",e);
        } finally {
            if (null!=fileWriter){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Async
    @Override
    public void eavesdrop(String projectCode, String username, String listenerExt) {
        UserinfoPojo agent = this.userInfoServiceApi.getByUsername(username);
        UserinfoPojo listener = this.userInfoServiceApi.getByUsername(listenerExt);
        if (projectCode == null){
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(inboundClient.option().serverOptions().get(0).addr(),
                    "create_uuid",null);
            listener.setCalluuid(eslMessage.getBodyLines().get(0));

            String callStr= "{origination_uuid="+eslMessage.getBodyLines().get(0)+"}user/"+listener.getUsername()+" &eavesdrop("+agent.getCalluuid()+")";
            logger.info("监听坐席通话{}",inboundClient.sendAsyncApiCommand(inboundClient.option().serverOptions().get(0).addr(),"originate",callStr));

        }else {
            CallProjectPojo callProjectPojo = new CallProjectPojo();
            callProjectPojo.setProjectCode(projectCode);
            callProjectPojo = this.selectUnique(callProjectPojo);

            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
                    "create_uuid",null);
            listener.setCalluuid(eslMessage.getBodyLines().get(0));

            String callStr= "{origination_uuid="+eslMessage.getBodyLines().get(0)+"}user/"+listener.getUsername()+" &eavesdrop("+agent.getCalluuid()+")";
            logger.info("监听坐席通话{}",inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"originate",callStr));

        }

        this.userInfoServiceApi.update(listener);
    }

    @Async
    @Override
    public void conference(String projectCode, String listenerExt) {
        UserinfoPojo listener = this.userInfoServiceApi.getByUsername(listenerExt);
        CallProjectPojo callProjectPojo = new CallProjectPojo();
        callProjectPojo.setProjectCode(projectCode);
        callProjectPojo = this.selectUnique(callProjectPojo);
        EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
                "conference", projectCode +" list");
        if (eslMessage.getBodyLines().get(0).contains("not found")){
            inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference", projectCode +" bgdial user/"+ listenerExt);
            logger.info("项目经理{}发起会议{}", listenerExt, projectCode);

            List<UserinfoPojo> agents = this.userInfoServiceApi.getListByProjectIdAndStatus(callProjectPojo.getId(),1);
            CallProjectPojo finalCallProjectPojo = callProjectPojo;
            agents.forEach(e->{
                if (e.getSessionStatus().equals(0)&&e.getWorkStatus().equals(0)){
                    inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(finalCallProjectPojo.getFreeswitchId()),"conference", projectCode +" bgdial user/"+e.getUsername());
                    logger.info("邀请坐席{}加入会议{}",e.getUsername(), projectCode);
                }
            });
        }else {
            inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference", projectCode +" bgdial user/"+ listenerExt);
            logger.info("项目经理{}加入会议{}", listenerExt, projectCode);
        }
    }
}
