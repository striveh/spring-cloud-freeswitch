package com.striveh.callcenter.gateway.filter;

import com.striveh.callcenter.gateway.sys.service.iservice.ISysAuthInfoService;
import com.striveh.callcenter.common.base.pojo.OptResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthorizeGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizeGatewayFilterFactory.Config> {

    private static final Log logger = LogFactory.getLog(AuthorizeGatewayFilterFactory.class);

    @Autowired
    private ISysAuthInfoService sysAuthInfoService;

    public AuthorizeGatewayFilterFactory() {
        super(Config.class);
        logger.info("Loaded GatewayFilterFactory [Authorize]");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    @Override
    public GatewayFilter apply(AuthorizeGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String account = headers.getFirst("account");
            String sign = headers.getFirst("sign");
            String timestamp = headers.getFirst("timestamp");
            if (account == null) {
                account = request.getQueryParams().getFirst("account");
            }
            if (sign == null) {
                sign = request.getQueryParams().getFirst("sign");
            }
            if (timestamp == null) {
                timestamp = request.getQueryParams().getFirst("timestamp");
            }

            OptResult result = sysAuthInfoService.chenkSign(account, timestamp, sign, 1);

            if (result.getCode()<0) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 控制是否开启认证
        private boolean enabled;

        public Config() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}