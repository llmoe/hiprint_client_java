package com.llmoe.print.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Socket配置
 *
 * @author llmoe
 * @date 2020/6/6 10:46
 */
@Configuration
public class SocketConfig {

    /**
     * 监听地址
     */
    @Value("${socketio.host}")
    private String host;

    /**
     * 端口
     */
    @Value("${socketio.port}")
    private Integer port;

    /**
     * socket线程组大小（如只监听一个端口boss线程组为1即可）
     */
    @Value("${socketio.bossCount}")
    private int bossCount;

    @Value("${socketio.workCount}")
    private int workCount;

    @Value("${socketio.allowCustomRequests}")
    private boolean allowCustomRequests;

    /**
     * 协议升级超时时间（毫秒），默认10秒。HTTP握手升级为ws协议超时时间
     */
    @Value("${socketio.upgradeTimeout}")
    private int upgradeTimeout;

    /**
     * Ping消息超时时间（毫秒），默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
     */
    @Value("${socketio.pingTimeout}")
    private int pingTimeout;

    /**
     * Ping消息间隔（毫秒），默认25秒。客户端向服务器发送一条心跳消息间隔
     */
    @Value("${socketio.pingInterval}")
    private int pingInterval;

    /**
     * 设置http交互最大内容长度
     */
    @Value("${socketio.maxHttpContentLength}")
    private int maxHttpContentLength;

    /**
     * 设置最大每帧处理数据的长度，防止他人利用大数据来攻击服务器
     */
    @Value("${socketio.maxFramePayloadLength}")
    private int maxFramePayloadLength;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
//        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }
}
