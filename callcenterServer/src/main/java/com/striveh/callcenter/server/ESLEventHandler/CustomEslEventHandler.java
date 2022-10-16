
package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.server.ESLEventHandler.customSubEvent.ISubEventHandler;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@EslEventName(EventNames.CUSTOM)
@Component
public class CustomEslEventHandler implements EslEventHandler {
    private InboundClient inboundClient;
    @Autowired
    private ApplicationContext applicationContext;


    protected Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("CustomEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);

        try{
            ISubEventHandler subEventHandler = applicationContext.getBean(event.getEventHeaders().get("Event-Subclass").replace("::","")+"EventHandler",ISubEventHandler.class);
            subEventHandler.handle(addr,event);
        }catch (NoSuchBeanDefinitionException ex){
            log.warn("{}没实现",event.getEventHeaders().get("Event-Subclass").replace("::","")+"EventHandler");
        }
    }
}
