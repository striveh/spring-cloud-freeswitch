package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EslEventName(EventNames.CHANNEL_ANSWER)
@Component
public class ChannelAnswerEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private SimpMessageSendingOperations msgOperations;

    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;

    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;

    @Autowired
    private ICallProjectService callProjectService;

    @Autowired
    @Lazy
    private ICallTaskService callTaskService;
    @Autowired
    private ICallLogService callLogService;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("ChannelAnswerEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));

        //根据variable_return_visit判断是否是回访、根据呼叫号码是否大于等于11位判断是否是呼向手机用户的
        if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_return_visit"))
        &&event.getEventHeaders().get("Caller-Destination-Number").length()>=11){

            String telNo=event.getEventHeaders().get("Caller-Destination-Number");
            String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
            if (phonePrefix==null){
                phonePrefix="";
            }
            if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                telNo=telNo.replaceFirst(phonePrefix,"");
            }
            Map<String,String> body=new HashMap<>();
            body.put("telNo",telNo);
            body.put("callId", event.getEventHeaders().get("Unique-ID"));
            body.put("callTaskCode", event.getEventHeaders().get("variable_callcenter_task_code"));

            msgOperations.convertAndSend("/topic/invite/" + event.getEventHeaders().get("Other-Leg-Caller-ID-Number"),  JsonTool.getJsonString(body));

            UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Other-Leg-Caller-ID-Number"));
            userinfoPojo.setSessionStatus(10);
            userinfoPojo.setLastSessionBeginTime(DateTool.getTimestamp());
            userinfoPojo.setDestinationNumber(body.get("telNo"));
            userinfoPojo.setCallTaskCode(body.get("callTaskCode"));
            userinfoPojo.setDestinationUUID(body.get("callId"));
            userinfoPojo.setStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setCalluuid(event.getEventHeaders().get("Other-Leg-Unique-ID"));
            this.userInfoServiceApi.update(userinfoPojo);

        }else if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_preview_call"))
                &&event.getEventHeaders().get("Caller-Destination-Number").length()>=11){
            String telNo=event.getEventHeaders().get("Caller-Destination-Number");
            String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
            if (phonePrefix==null){
                phonePrefix="";
            }
            if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                telNo=telNo.replaceFirst(phonePrefix,"");
            }
            Map<String,String> body=new HashMap<>();
            body.put("telNo",telNo);
            body.put("callId", event.getEventHeaders().get("Unique-ID"));
            body.put("callTaskCode", event.getEventHeaders().get("variable_callcenter_task_code"));

            msgOperations.convertAndSend("/topic/invite/" + event.getEventHeaders().get("Other-Leg-Caller-ID-Number"),  JsonTool.getJsonString(body));


            UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Other-Leg-Caller-ID-Number"));
            userinfoPojo.setSessionStatus(10);
//            userinfoPojo.setWorkStatus(1);
            userinfoPojo.setLastSessionBeginTime(DateTool.getTimestamp());

            baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+
                    body.get("callTaskCode"),body.get("telNo"));
            baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+
                    body.get("callTaskCode"),body.get("telNo"),30*60);
            msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);

            userinfoPojo.setDestinationNumber(body.get("telNo"));
            userinfoPojo.setCallTaskCode(body.get("callTaskCode"));
            userinfoPojo.setDestinationUUID(body.get("callId"));
            msgOperations.convertAndSend("/topic/callExtStatus/" + event.getEventHeaders().get("variable_callcenter_project_code"), userinfoPojo);
            UserinfoPojo notify = userinfoPojo;
            this.callTaskService.notifyAgentStatus(notify);
            userinfoPojo.setStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setCalluuid(event.getEventHeaders().get("Other-Leg-Unique-ID"));
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}，{}通话开始{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Other-Leg-Unique-ID"),event.getEventHeaders().get("Caller-Destination-Number"));
        }else if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_call_transfer"))){
            String telNo=event.getEventHeaders().get("Other-Leg-Destination-Number");
            String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
            if (phonePrefix==null){
                phonePrefix="";
            }
            if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                telNo=telNo.replaceFirst(phonePrefix,"");
            }
            Map<String,String> body=new HashMap<>();
            body.put("telNo",telNo);
            body.put("callId", event.getEventHeaders().get("Other-Leg-Unique-ID"));

            CallLogPojo callLogPojo = new CallLogPojo();
            callLogPojo.setCallUUID(event.getEventHeaders().get("Other-Leg-Unique-ID"));
            callLogPojo=this.callLogService.selectUnique(callLogPojo);
            body.put("callTaskCode", callLogPojo.getCallTaskCode());
            body.put("username", event.getEventHeaders().get("Other-Leg-Callee-ID-Number"));
            msgOperations.convertAndSend("/topic/transfer/" + event.getEventHeaders().get("Caller-Destination-Number"),  JsonTool.getJsonString(body));
            Map<String,String> notify = new HashMap<>();
            notify.put("type","message");
            notify.put("duration","3000");
            notify.put("data","有转接来电："+telNo);
            msgOperations.convertAndSend("/topic/notify/" + event.getEventHeaders().get("Caller-Destination-Number"), JsonTool.getFormatJsonString(notify));

            UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Caller-Destination-Number"));
            userinfoPojo.setSessionStatus(10);
//            userinfoPojo.setWorkStatus(1);
            userinfoPojo.setLastSessionBeginTime(DateTool.getTimestamp());

            msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);

            userinfoPojo.setDestinationNumber(body.get("telNo"));
            userinfoPojo.setCallTaskCode(body.get("callTaskCode"));
            userinfoPojo.setDestinationUUID(body.get("callId"));

            userinfoPojo.setStatus(null);
            userinfoPojo.setWorkStatus(null);
            userinfoPojo.setCalluuid(event.getEventHeaders().get("Unique-ID"));
            this.userInfoServiceApi.update(userinfoPojo);
            log.info("坐席{}，{}通话开始{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Other-Leg-Unique-ID"),event.getEventHeaders().get("Other-Leg-Destination-Number"));
        }

    }
}
