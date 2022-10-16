package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
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

@EslEventName(EventNames.MEDIA_BUG_STOP)
@Component
public class MediaBugStopEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    InboundClient inboundClient;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("MediaBugStopEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));

        // 把坐席静音恢复
        UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(event.getEventHeaders().get("Other-Leg-Destination-Number"));
        if (userinfoPojo!=null){
            EslMessage uuid_audioForAgent = inboundClient.sendSyncApiCommand(addr,"uuid_audio",userinfoPojo.getCalluuid()+" stop");
            log.info("坐席{}uuid_audioForAgent:{}结果{}",event.getEventHeaders().get("Other-Leg-Destination-Number"),userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_audioForAgent));
        }
    }
}
