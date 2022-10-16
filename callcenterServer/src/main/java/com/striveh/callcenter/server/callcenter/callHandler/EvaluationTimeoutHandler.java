package com.striveh.callcenter.server.callcenter.callHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class EvaluationTimeoutHandler extends KeyExpirationEventMessageListener {

    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    InboundClient inboundClient;

    public EvaluationTimeoutHandler(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("redis key过期：{}", expiredKey);
        String [] params = expiredKey.split("_");
        if (expiredKey.contains(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION_PLAYBACK.getCode())){
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(params[2], "uuid_kill",params[1]);
            log.info("挂断{}结果{}",params[1], EslHelper.formatEslMessage(eslMessage));
        }

        super.onMessage(message, pattern);
    }
}
