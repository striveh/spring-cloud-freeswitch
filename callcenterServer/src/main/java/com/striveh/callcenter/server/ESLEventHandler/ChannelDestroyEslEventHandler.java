package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EslEventName(EventNames.CHANNEL_DESTROY)
@Component
public class ChannelDestroyEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;
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
        log.info("ChannelDestroyEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
        try {
            if (StringUtils.isNotEmpty(event.getEventHeaders().get("variable_callcenter_task_code"))){
                String telNo=event.getEventHeaders().get("Caller-Destination-Number");
                if (StringUtils.isNotEmpty(telNo)&&telNo.length()>11){
                    String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                    if (phonePrefix==null){
                        phonePrefix="";
                    }
                    if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                        telNo=telNo.replaceFirst(phonePrefix,"");
                    }
                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+
                            event.getEventHeaders().get("variable_callcenter_task_code"),telNo);
                }else {
                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+
                            event.getEventHeaders().get("variable_callcenter_task_code"),telNo);
                }
            }else {
                if (StringUtils.isEmpty(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"))
                        &&StringUtils.isEmpty(event.getEventHeaders().get("variable_return_visit"))
                        &&StringUtils.isEmpty(event.getEventHeaders().get("variable_preview_call"))){
                    UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("variable_dialed_user"));
                    if (userinfoPojo!=null&&!userinfoPojo.getWorkStatus().equals(-1)&&userinfoPojo.getType().equals(2)){
                        if (StringUtils.isNotEmpty(baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername(),String.class))){
                            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername());
                            Map<String,String> notify = new HashMap<>();
                            notify.put("type","message");
                            notify.put("duration","5000");
                            notify.put("data","置闲失败，"+event.getEventHeaders().get("Hangup-Cause"));
                            notify.put("msgType","error");
                            msgOperations.convertAndSend("/topic/notify/" + userinfoPojo.getUsername(), JsonTool.getFormatJsonString(notify));
                        }else {
                            userinfoPojo.setWorkStatus(0);
                            userinfoPojo.setSessionStatus(0);
                            msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);

                            CallProjectPojo callProjectPojo = new CallProjectPojo();
                            callProjectPojo.setId(userinfoPojo.getProjectId());
                            callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
                            msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
                            UserinfoPojo notify = userinfoPojo;
                            this.callTaskService.notifyAgentStatus(notify);

                            userinfoPojo.setStatus(null);
                            userinfoPojo.setProjectId(null);
                            this.userInfoServiceApi.update(userinfoPojo);
                            log.info("坐席{}置忙，call_uuid:{}",userinfoPojo.getUsername(),event.getEventHeaders().get("variable_call_uuid"));
                        }
                    }
                }else if (StringUtils.isNotEmpty(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"))){
                    UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("variable_dialed_user"));
                    //11660呼入项目拒接处理
                    if (userinfoPojo.getProjectId().equals(7145L)&&userinfoPojo.getSessionStatus().equals(0)){
                        Map<String,String> body=new HashMap<>();
                        body.put("telNo",userinfoPojo.getDestinationNumber());
                        msgOperations.convertAndSend("/topic/bye/" + userinfoPojo.getUsername(), JsonTool.getJsonString(body));
                        msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
                    }
                }
            }
        }catch (Exception e){
            log.error("ChannelDestroyEslEventHandler处理异常",e);
        }
    }
}
