package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component("sofiaunregisterEventHandler")
public class UnregisterEventHandler implements ISubEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private InboundClient inboundClient;
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private ICallProjectService callProjectService;
    @Autowired
    @Lazy
    private ICallTaskService callTaskService;
    @Autowired
    private SimpMessageSendingOperations msgOperations;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("sofiaunregisterEventHandler:{},{}",addr, EslHelper.formatEslEvent(event));
        UserinfoPojo userinfoPojo=this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("from-user"));
        if (userinfoPojo!=null&&userinfoPojo.getType().equals(2)){
            //通话中超过3分钟，我们认为是异常了
            if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&userinfoPojo.getSessionStatus().equals(10)&&
                    (DateTool.getTimestamp().getTime()-userinfoPojo.getLastSessionBeginTime().getTime())>1000*60*3){
                EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, "uuid_kill",userinfoPojo.getCalluuid());
                log.info("摘机坐席{}注销通话中置忙{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
                userinfoPojo.setWorkStatus(0);
            }else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&userinfoPojo.getWorkStatus().equals(1)
                    &&userinfoPojo.getSessionStatus().equals(0)){
                EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, "uuid_kill",userinfoPojo.getCalluuid());
                log.info("摘机坐席{}注销置忙{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
                userinfoPojo.setWorkStatus(0);
            }
            CallProjectPojo callProjectPojo = new CallProjectPojo();
            callProjectPojo.setId(userinfoPojo.getProjectId());
            callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);

            CallTaskPojo callTask = new CallTaskPojo();
            callTask.setProjectCode(callProjectPojo.getProjectCode());
            callTask = this.callTaskService.getProc(callTask);
            if (callTask!=null &&!callTask.getScheduleType().equals(4)&& (callTask.getStatus().equals(2)||callTask.getStatus().equals(3))) {
                String param=callTask.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
                        "sip_h_X-Call-Task-Code="+callTask.getCallTaskCode()+"}user/"+userinfoPojo.getUsername();
                EslMessage eslMessage =inboundClient.sendSyncApiCommand(addr,"fifo_member del", param );
                log.info("{}已注销，从呼叫队列移除{}结果{}",userinfoPojo.getUsername(),param, EslHelper.formatEslMessage(eslMessage));
                userinfoPojo.setWorkStatus(0);
            }
            userinfoPojo.setStatus(0);
            msgOperations.convertAndSend("/topic/register/" + event.getEventHeaders().get("from-user"), userinfoPojo);

            msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
            UserinfoPojo notify = userinfoPojo;
            this.callTaskService.notifyAgentStatus(notify);
            userinfoPojo.setProjectId(null);
            userinfoPojo.setSessionStatus(null);
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}注销",event.getEventHeaders().get("from-user"));
        }else if (userinfoPojo!=null&&userinfoPojo.getType().equals(3)){
            userinfoPojo.setStatus(0);
            msgOperations.convertAndSend("/topic/register/" + event.getEventHeaders().get("from-user"), userinfoPojo);
            userinfoPojo.setSessionStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setProjectId(null);
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}注销",event.getEventHeaders().get("from-user"));
        }

    }
}
