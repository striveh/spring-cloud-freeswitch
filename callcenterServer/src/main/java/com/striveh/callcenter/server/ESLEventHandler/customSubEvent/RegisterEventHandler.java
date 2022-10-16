package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component("sofiaregisterEventHandler")
public class RegisterEventHandler implements ISubEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private SimpMessageSendingOperations msgOperations;
    @Autowired
    private ICallProjectService callProjectService;
    @Autowired
    @Lazy
    private ICallTaskService callTaskService;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("sofiaregisterEventHandler:{},{}",addr, event);

        UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("from-user"));
        if (userinfoPojo!=null&&userinfoPojo.getType().equals(2)){
            userinfoPojo.setStatus(1);
            msgOperations.convertAndSend("/topic/register/" + event.getEventHeaders().get("from-user"), userinfoPojo);
            CallProjectPojo callProjectPojo = new CallProjectPojo();
            callProjectPojo.setId(userinfoPojo.getProjectId());
            callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
            msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
            UserinfoPojo notify = userinfoPojo;
            this.callTaskService.notifyAgentStatus(notify);
            userinfoPojo.setSessionStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setProjectId(null);
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}注册",event.getEventHeaders().get("from-user"));
        }else if (userinfoPojo!=null&&userinfoPojo.getType().equals(3)){
            userinfoPojo.setStatus(1);
            msgOperations.convertAndSend("/topic/register/" + event.getEventHeaders().get("from-user"), userinfoPojo);
            userinfoPojo.setSessionStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setProjectId(null);
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}注册",event.getEventHeaders().get("from-user"));
        }
    }
}
