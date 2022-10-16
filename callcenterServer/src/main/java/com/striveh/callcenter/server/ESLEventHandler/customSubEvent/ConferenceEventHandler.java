package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component("conferencemaintenanceEventHandler")
public class ConferenceEventHandler implements ISubEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private InboundClient inboundClient;
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
        UserinfoPojo userinfoPojo = null;

        switch (event.getEventHeaders().get("Action")) {
            case "conference-create":
                log.info("ConferenceEventHandler conference-create:{},{}", addr, EslHelper.formatEslEvent(event));
                String projectCode = event.getEventHeaders().get("Conference-Name");
//                CallProjectPojo callProjectPojo = new CallProjectPojo();
//                callProjectPojo.setProjectCode(projectCode);
//                callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
//                List<UserinfoPojo> agents = this.userInfoServiceApi.getListByProjectIdAndStatus(callProjectPojo.getId(),1);
//                agents.forEach(e->{
//                    inboundClient.sendAsyncApiCommand(addr,"conference",projectCode+" bgdial user/"+e.getUsername());
//                    log.info("邀请坐席{}加入会议{}",e.getUsername(),projectCode);
//                });
                break;
            case "add-member":
                log.info("ConferenceEventHandler add-member:{},{}", addr, EslHelper.formatEslEvent(event));

                userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Caller-Destination-Number"));
                userinfoPojo.setSessionStatus(7);

                msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
                msgOperations.convertAndSend("/topic/callExtStatus/" + event.getEventHeaders().get("Conference-Name"), userinfoPojo);
                UserinfoPojo notify = userinfoPojo;
                this.callTaskService.notifyAgentStatus(notify);

                userinfoPojo.setStatus(null);
                this.userInfoServiceApi.update(userinfoPojo);

                log.info("坐席{}加入会议{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Conference-Name"));

                break;
            case "del-member":
                log.info("ConferenceEventHandler del-member:{},{}", addr, EslHelper.formatEslEvent(event));

                userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Caller-Destination-Number"));
                userinfoPojo.setSessionStatus(0);

                msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
                msgOperations.convertAndSend("/topic/callExtStatus/" + event.getEventHeaders().get("Conference-Name"), userinfoPojo);
                UserinfoPojo notify1 = userinfoPojo;
                this.callTaskService.notifyAgentStatus(notify1);

                userinfoPojo.setStatus(null);
                this.userInfoServiceApi.update(userinfoPojo);

                log.info("坐席{}离开会议{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Conference-Name"));

                if (userinfoPojo.getType().equals(3)){
                    inboundClient.sendAsyncApiCommand(addr,"conference",event.getEventHeaders().get("Conference-Name")+" hup all");
                    log.info("项目经理{}离开离开会议,会议{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Conference-Name"));
                }

                break;
            default:
                log.info("ConferenceEventHandler unknown:{},{}", addr, EslHelper.formatEslEvent(event));
        }
    }
}
