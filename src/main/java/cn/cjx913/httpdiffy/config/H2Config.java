package cn.cjx913.httpdiffy.config;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class H2Config {
    @Autowired
    private HttpDiffyProperties properties;

    private org.h2.tools.Server webServer;
//    private org.h2.tools.Server tcpServer;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        this.webServer = org.h2.tools.Server.createWebServer("-webPort", properties.getH2WebServerPort(), "-tcpAllowOthers").start();
//        this.tcpServer = org.h2.tools.Server.createTcpServer("-tcpPort", properties.getH2TcpServerPort(), "-tcpAllowOthers").start();
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
//        this.tcpServer.stop();
        this.webServer.stop();

    }
}
