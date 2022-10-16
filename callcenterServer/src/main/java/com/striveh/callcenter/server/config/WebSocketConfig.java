package com.striveh.callcenter.server.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * WebSocket 配置
 * EnableWebSocketMessageBroker 注解开启使用 STOMP 协议来传输基于代理的消息，使 Controller 支持 MessageMapping 注解。
 */
@Configuration
@EnableWebSocketMessageBroker
@ConfigurationProperties(prefix = "websocket")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, ApplicationListener<BrokerAvailabilityEvent> {

    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private BrokerConfig brokerConfig;
    @Autowired
    private WebSocketDecoratorFactory webSocketDecoratorFactory;
    private String[] allowOrigins;

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebSocketMessageBrokerStats) {
                    WebSocketMessageBrokerStats webSocketMessageBrokerStats = (WebSocketMessageBrokerStats) bean;
                    webSocketMessageBrokerStats.setLoggingPeriod(30 * 1000); // your customization
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 继承DefaultHandshakeHandler并重写determineUser方法，可以自定义如何确定用户
        // 添加方法：registry.addEndpoint("/ws").setHandshakeHandler(handshakeHandler)
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowOrigins)
                .withSockJS();
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        if (brokerConfig.isUseSimpleBroker()) {
            // 使用 SimpleBroker
            // 配置前缀, 有这些前缀的消息会路由到broker
            registry.enableSimpleBroker("/topic", "/queue")
                    //配置stomp协议里, server返回的心跳
                    .setHeartbeatValue(new long[]{10000L, 10000L})
                    //配置发送心跳的scheduler
                    .setTaskScheduler(new DefaultManagedTaskScheduler());
        } else {
            // 使用外部 Broker
            // 指定前缀，有这些前缀的消息会路由到broker
            registry.enableStompBrokerRelay("/topic", "/queue")
                    // 广播用户目标，如果要推送的用户不在本地，则通过 broker 广播给集群的其他成员
                    .setUserDestinationBroadcast("/topic/log-unresolved-user")
                    // 用户注册广播，一旦有用户登录，则广播给集群中的其他成员
                    .setUserRegistryBroadcast("/topic/log-user-registry")
                    // 虚拟地址
                    .setVirtualHost(brokerConfig.getVirtualHost())
                    // 用户密码
                    .setSystemLogin(brokerConfig.getUsername())
                    .setSystemPasscode(brokerConfig.getPassword())
                    .setClientLogin(brokerConfig.getUsername())
                    .setClientPasscode(brokerConfig.getPassword())
                    // 心跳间隔
                    .setSystemHeartbeatSendInterval(10000)
                    .setSystemHeartbeatReceiveInterval(10000)
                    // 使用 setTcpClient 以配置多个 broker 地址，setRelayHost/Port 只能配置一个
                    .setTcpClient(createTcpClient());
        }
    }

    /**
     * 创建 TcpClient 工厂，用于配置多个 broker 地址
     */
    private ReactorNettyTcpClient<byte[]> createTcpClient() {
        return new ReactorNettyTcpClient<>(
                // BrokerAddressSupplier 用于获取中继地址，一次只使用一个，如果该中继出错，则会获取下一个
                client -> client.addressSupplier(brokerConfig.getBrokerAddressSupplier()),
                new StompReactorNettyCodec());
    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        if (!event.isBrokerAvailable()) {
            log.warn("stomp broker is not available!!!!!!!!");
        } else {
            log.info("stomp broker is available");
        }
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(webSocketDecoratorFactory);
        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registry);
    }

    public void setAllowOrigins(String[] allowOrigins) {
        this.allowOrigins = allowOrigins;
    }
}
