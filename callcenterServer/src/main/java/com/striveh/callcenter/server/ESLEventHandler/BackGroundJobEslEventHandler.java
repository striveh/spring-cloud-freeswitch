
package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@EslEventName(EventNames.BACKGROUND_JOB)
@Component
public class BackGroundJobEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private ICallLogService callLogService;
    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;
    @Autowired
    InboundClient inboundClient;
    @Autowired
    private SimpMessageSendingOperations msgOperations;
    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("BackGroundJobEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
        try {

            if (event.getEventBodyLines().get(0).contains("GATEWAY_DOWN")
                    ||event.getEventBodyLines().get(0).contains("DESTINATION_OUT_OF_ORDER")
                    ||event.getEventBodyLines().get(0).contains("USER_NOT_REGISTERED")) {
                //针对坐席置闲的处理、根据呼叫字符串里是否包含 answer,fifo:
                if (event.getEventHeaders().get("Job-Command-Arg").contains("answer,fifo:")){
                    String callStr=event.getEventHeaders().get("Job-Command-Arg");
                    String username = callStr.substring(callStr.lastIndexOf("/")+1,callStr.lastIndexOf("answer")-1);
                    baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+username);

                    Map<String,String> notify = new HashMap<>();
                    notify.put("type","message");
                    notify.put("duration","5000");
                    notify.put("data","置闲失败，"+event.getEventBodyLines().get(0));
                    notify.put("msgType","error");
                    msgOperations.convertAndSend("/topic/notify/" + username, JsonTool.getFormatJsonString(notify));
                    return;
                }

                EslMessage message = inboundClient.sendSyncApiCommand(addr,"status",null);
                log.info("DESTINATION_OUT_OF_ORDER FreeSWITCH:{}status:{}",addr, EslHelper.formatEslMessage(message));

                String callStr=event.getEventHeaders().get("Job-Command-Arg");
                CallLogPojo callLogPojo = new CallLogPojo();
                callLogPojo.setCallTaskCode(callStr.substring(callStr.lastIndexOf("=")+1,callStr.indexOf("}")));
                String telNo = null;
                if (callStr.contains("sofia/gateway")){
                    telNo=callStr.substring(callStr.lastIndexOf("/")+1,callStr.lastIndexOf("fifo")-1);
                }else {
                    telNo=callStr.substring(callStr.lastIndexOf("/")+1,callStr.lastIndexOf("@"));
                }
                if (StringUtils.isNotEmpty(telNo) && telNo.length() > 11) {
                    String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                    if (phonePrefix==null){
                        phonePrefix="";
                    }
                    if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                        telNo=telNo.replaceFirst(phonePrefix,"");
                    }
                    callLogPojo.setDestinationNumber(telNo);
                } else {
                    callLogPojo.setDestinationNumber(telNo);
                }
                callLogPojo = this.callLogService.selectUnique(callLogPojo);
                if (callLogPojo != null) {
                    //让任务暂停10秒
                    baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_PAUSE.getCode()+callLogPojo.getCallTaskCode(),10,10);

                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callLogPojo.getCallTaskCode() + "_" + callLogPojo.getGwCode(), callLogPojo.getDestinationNumber());
                    baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+ callLogPojo.getCallTaskCode(), callLogPojo.getDestinationNumber());

                    callLogPojo.setResultDetail("10");
                    callLogPojo.setResult("20");
                    callLogPojo.setStartTimestamp(DateTool.getTimestamp());
                    callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                    callLogPojo.setHangupCase(event.getEventBodyLines().get(0));
                    callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    callLogPojo.setCallUUID(StringTool.getSerno());

                    this.callLogService.update(callLogPojo);
                }
            }
        }catch (Exception ex){
            log.error("BackGroundJobEslEventHandler处理异常。。。",ex);
        }
    }
}
