
package com.striveh.callcenter.server.ESLEventHandler;

import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@EslEventName(EventNames.HEARTBEAT)
@Component
public class HeartbeatEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    InboundClient inboundClient;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("HeartbeatEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
        EslMessage message_status = inboundClient.sendSyncApiCommand(addr,"status",null);
        log.info("FreeSWITCH:{}status:{}",addr, EslHelper.formatEslMessage(message_status));
        EslMessage message_registers = inboundClient.sendSyncApiCommand(addr,"sofia status profile internal reg",null);

        AtomicReference<Integer> wssCount = new AtomicReference<>(0);
        AtomicReference<Integer> udpCount = new AtomicReference<>(0);

        message_registers.getBodyLines().forEach(e->{
            if (e.contains("Contact:")){
                if (e.contains("transport=wss")){
                    wssCount.getAndSet(wssCount.get() + 1);
                }else {
                    udpCount.getAndSet(udpCount.get() + 1);
                }
            }
        });
        log.info("FreeSWITCH:{},Registrations:{},wss:{},udp:{}",addr, message_registers.getBodyLines().get(message_registers.getBodyLines().size()-2),wssCount.get(),udpCount.get());
    }
}
