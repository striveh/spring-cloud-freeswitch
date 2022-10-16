package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@EslEventName(EventNames.CHANNEL_PARK)
@Component
public class ChannelParkEslEventHandler implements EslEventHandler {

    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private InboundClient inboundClient;
    @Autowired
    private ICallLogService callLogService;

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("ChannelParkEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
//        if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_call_in"))){
//            //记录calllog
//            CallLogPojo callLogPojo=new CallLogPojo();
//            callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
//            callLogPojo.setCallTaskCode("1");
//            callLogPojo.setProjectCode("1");
////        callLogPojo.setGwCode(gatewayPojo.getGwCode());
////        if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
////            callLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
////        }
//            callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
//            callLogPojo.setDestinationNumber(event.getEventHeaders().get("variable_sip_to_user"));
//            callLogPojo.setCallUUID(event.getEventHeaders().get("variable_call_uuid"));
//            callLogPojo.setHangupCase(event.getEventHeaders().get("variable_sip_from_user"));
//            callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            this.callLogService.insert(callLogPojo);
//
//            log.info("呼入执行fifo结果{}",inboundClient.sendSyncApiCommand(addr, "fifo","1 in"));
//        }

    }
}