package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@EslEventName(EventNames.CHANNEL_CREATE)
@Component
public class ChannelCreateEslEventHandler implements EslEventHandler {

    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private ICallLogService callLogService;

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("ChannelCreateEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);
        if (StringUtils.isNotEmpty(event.getEventHeaders().get("variable_callcenter_project_code"))
                &&StringUtils.isEmpty(event.getEventHeaders().get("variable_return_visit"))
                &&StringUtils.isEmpty(event.getEventHeaders().get("variable_preview_call"))){
            CallLogPojo callLogPojo = new CallLogPojo();
            callLogPojo.setProjectCode(event.getEventHeaders().get("variable_callcenter_project_code"));
            callLogPojo.setCallTaskCode(event.getEventHeaders().get("variable_callcenter_task_code"));
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
                callLogPojo.setDestinationNumber(telNo);
            }else {
                callLogPojo.setDestinationNumber(telNo);
            }
            callLogPojo=this.callLogService.selectUnique(callLogPojo);
            if (callLogPojo!=null) {
                callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setCallerIdNumber(event.getEventHeaders().get("Other-Leg-Destination-Number"));
                callLogPojo.setCallUUID(event.getEventHeaders().get("variable_call_uuid"));
                callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                this.callLogService.update(callLogPojo);
            }
        }
    }
}
