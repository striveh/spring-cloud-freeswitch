package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@EslEventName(EventNames.DTMF)
@Component
public class DTMFEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    InboundClient inboundClient;
    @Autowired
    @Qualifier("baseRedisDaoDef")
    BaseRedisDao baseRedisDao;
    @Value("${common.playback.path}")
    private String playbackPath;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("DTMFEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));

        Integer evaluation = baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION_PLAYBACK.getCode()+ event.getEventHeaders().get("Caller-Unique-ID")+"_"+addr,Integer.class);
        if (evaluation!=null&&evaluation.equals(1)){
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION_PLAYBACK.getCode()+ event.getEventHeaders().get("Caller-Unique-ID"));
            String telNo=event.getEventHeaders().get("Caller-Destination-Number");
            String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
            if (phonePrefix==null){
                phonePrefix="";
            }
            if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                telNo=telNo.replaceFirst(phonePrefix,"");
            }
            String dtmfDigit = event.getEventHeaders().get("DTMF-Digit");
            if (StringUtils.isNumeric(dtmfDigit)){
                baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION.getCode()+ telNo,dtmfDigit,60);
            }
            EslMessage eslMessagePlay = inboundClient.sendSyncApiCommand(addr, "uuid_broadcast",event.getEventHeaders().get("Caller-Unique-ID")+" "+playbackPath+"bye.mp3 aleg");
            log.info("播放挂断{}结果{}",event.getEventHeaders().get("Caller-Unique-ID"), EslHelper.formatEslMessage(eslMessagePlay));
        }
    }
}
