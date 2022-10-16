package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component("fifoinfoEventHandler")
public class FIFOEventHandler implements ISubEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private InboundClient inboundClient;
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private SimpMessageSendingOperations msgOperations;
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
        try {
            UserinfoPojo userinfoPojo = null;
            Map<String,String> body=null;
            if (event.getEventHeaders().get("FIFO-Action").equals("bridge-caller-start")||event.getEventHeaders().get("FIFO-Action").equals("bridge-caller-stop")){
                String telNo=event.getEventHeaders().get("Caller-Destination-Number");
                String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                if (phonePrefix==null){
                    phonePrefix="";
                }
                if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                    telNo=telNo.replaceFirst(phonePrefix,"");
                }
                body=new HashMap<>();

                body.put("telNo",telNo);
                body.put("callId", event.getEventHeaders().get("variable_call_uuid"));

                body.put("callTaskCode", event.getEventHeaders().get("variable_callcenter_task_code"));
            }

            switch (event.getEventHeaders().get("FIFO-Action")){
                case "push":
                    log.info("fifoinfoEventHandler push:{},{}",addr, event);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(event.getEventHeaders().get("variable_call_in"))) {
                        //记录calllog
                        CallLogPojo callLogPojo = new CallLogPojo();
                        callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallTaskCode(event.getEventHeaders().get("variable_callcenter_task_code"));
                        callLogPojo.setProjectCode(event.getEventHeaders().get("variable_callcenter_project_code"));
                        callLogPojo.setGwCode(event.getEventHeaders().get("variable_sip_from_host"));

                        callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setDestinationNumber(event.getEventHeaders().get("variable_sip_to_user"));
                        callLogPojo.setCallUUID(event.getEventHeaders().get("variable_call_uuid"));
                        callLogPojo.setHangupCase(event.getEventHeaders().get("variable_sip_from_user"));
                        callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                        this.callLogService.insert(callLogPojo);
                    }
                    break;
                case "pre-dial":
                    log.info("fifoinfoEventHandler pre-dial:{},{}",addr, event);
                    break;
                case "post-dial":
                    log.info("fifoinfoEventHandler post-dial:{},{}",addr, event);
                    break;
                case "abort":
                    log.info("fifoinfoEventHandler abort:{},{}",addr, EslHelper.formatEslEvent(event));
                    break;
                case "consumer_start":
                    log.info("fifoinfoEventHandler consumer_start:{},{}",addr, EslHelper.formatEslEvent(event));
                    if (StringUtils.isEmpty(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"))){
                        userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("variable_dialed_user"));
                        if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())){
                            EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr,
                                    "uuid_kill",userinfoPojo.getCalluuid());
                            log.info("摘机坐席{}重复置闲{},杀掉上一个{}",userinfoPojo.getUsername(),userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(eslMessage));
                            if (!eslMessage.getBodyLines().get(0).contains("ERR")){
                                Thread.sleep(1000);
                            }
                        }
                        userinfoPojo.setWorkStatus(1);
                        userinfoPojo.setCalluuid(event.getEventHeaders().get("variable_call_uuid"));
                        msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);

                        CallProjectPojo callProjectPojo = new CallProjectPojo();
                        callProjectPojo.setId(userinfoPojo.getProjectId());
                        callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
                        msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
                        UserinfoPojo notify = userinfoPojo;
                        this.callTaskService.notifyAgentStatus(notify);
                        userinfoPojo.setStatus(null);
                        this.userInfoServiceApi.update(userinfoPojo);

                        baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername());
                        Map<String,String> msg = new HashMap<>();
                        msg.put("type","message");
                        msg.put("duration","3000");
                        msg.put("data","置闲成功");
                        msg.put("msgType","success");
                        msgOperations.convertAndSend("/topic/notify/" + userinfoPojo.getUsername(), JsonTool.getFormatJsonString(msg));

                        log.info("坐席{}置闲，call_uuid:{}",userinfoPojo.getUsername(),userinfoPojo.getCalluuid());
                    }
                    break;
                case "caller_pop":
                    log.info("fifoinfoEventHandler caller_pop:{},{}",addr,event);
                    break;
                case "consumer_pop":
                    log.info("fifoinfoEventHandler consumer_pop:{},{}",addr,event);
                    break;
                case "channel-consumer-start":
                    log.info("fifoinfoEventHandler channel-consumer-start:{},{}",addr, event);
                    break;
                case "bridge-consumer-start":
                    log.info("fifoinfoEventHandler bridge-consumer-start:{},{}",addr,event);
                    break;
                case "bridge-caller-start":
                    log.info("fifoinfoEventHandler bridge-caller-start:{},{}",addr, EslHelper.formatEslEvent(event));
                    msgOperations.convertAndSend("/topic/invite/" + event.getEventHeaders().get("Other-Leg-Caller-ID-Number"),  JsonTool.getJsonString(body));
                    userinfoPojo = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Other-Leg-Caller-ID-Number"));
                    userinfoPojo.setSessionStatus(10);
                    userinfoPojo.setWorkStatus(1);
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
                    break;
                case "channel-consumer-stop":
                    log.info("fifoinfoEventHandler channel-consumer-stop:{},{}",addr, event);

                    break;
                case "bridge-consumer-stop":
                    log.info("fifoinfoEventHandler bridge-consumer-stop:{},{}",addr, event);
                    break;
                case "bridge-caller-stop":
                    log.info("fifoinfoEventHandler bridge-caller-stop:{},{}",addr, EslHelper.formatEslEvent(event));
                    String username = event.getEventHeaders().get("Other-Leg-Caller-ID-Number");
                    if(StringUtils.isEmpty(username)){
                        username = event.getEventHeaders().get("Caller-Caller-ID-Number");
                    }
                    msgOperations.convertAndSend("/topic/bye/" + username, JsonTool.getJsonString(body));
                    userinfoPojo = this.userInfoServiceApi.getByUsername(username);
                    log.info("坐席{}通话结束{}",userinfoPojo.getUsername(),event.getEventHeaders().get("Caller-Destination-Number"));
                    userinfoPojo.setSessionStatus(0);
                    msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
                    msgOperations.convertAndSend("/topic/callExtStatus/" + event.getEventHeaders().get("variable_callcenter_project_code"), userinfoPojo);
                    UserinfoPojo notify1 = userinfoPojo;
                    this.callTaskService.notifyAgentStatus(notify1);
                    userinfoPojo.setStatus(null);
                    userinfoPojo.setWorkStatus(null);
                    userInfoServiceApi.update(userinfoPojo);

                    Integer busy = baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+userinfoPojo.getUsername(),Integer.class);
                    if (busy!=null){
                        baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+userinfoPojo.getUsername());
                        EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr,
                                "uuid_kill",userinfoPojo.getCalluuid());
                        log.info("摘机坐席{}置忙{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
                        if (busy.equals(-1)){
                            userinfoPojo.setWorkStatus(-1);
                            msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
                            msgOperations.convertAndSend("/topic/callExtStatus/" + event.getEventHeaders().get("variable_callcenter_project_code"), userinfoPojo);
                            UserinfoPojo notify2 = userinfoPojo;
                            this.callTaskService.notifyAgentStatus(notify2);
                            userinfoPojo.setStatus(null);
                            userInfoServiceApi.update(userinfoPojo);
                            log.info("摘机坐席{}被禁止",userinfoPojo.getUsername());
                        }
                    }
                    break;
                case "consumer_stop":
                    log.info("fifoinfoEventHandler consumer_stop:{},{},{}",addr, EslHelper.formatEslEvent(event),event.getEventHeaders().get("variable_last_sent_callee_id_number"));
                    break;
                default:
                    log.info("fifoinfoEventHandler 未知:{},{},{}",addr, EslHelper.formatEslEvent(event),event.getEventHeaders().get("FIFO-Action"));
            }
        }catch (Exception ex){
            log.error("fifoinfoEventHandler处理异常。。。",ex);
        }
    }
}
