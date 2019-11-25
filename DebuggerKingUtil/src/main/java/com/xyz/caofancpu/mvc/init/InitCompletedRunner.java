package com.xyz.caofancpu.mvc.init;

import com.xyz.caofancpu.mvc.config.CommonConfigValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Created by caofanCPU on 2018/7/20.
 */
@Component
@Order(value = 1)
@Slf4j
public class InitCompletedRunner implements CommandLineRunner, ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;
    private String serverIp;

    @Resource
    private transient CommonConfigValueService commonConfigPropertiesService;

    @Override
    public void run(String... strings) {
        log.info("项目启动成功, IP=" + this.serverIp + ", Port=" + serverPort);
        if (commonConfigPropertiesService.showApi) {
            log.info("SwaggerApi文档参见: http://" + this.serverIp + ":" + serverPort + commonConfigPropertiesService.contentPath + "/doc.html?plus=1&cache=1&filterApi=1&filterApiType=POST&lang=zh");
            log.info("官方原版SwaggerApi文档参见: http://" + this.serverIp + ":" + serverPort + "/swagger-ui.html");
            log.info("官方原版SwaggerApi认证key：Authorization");
        }
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.serverPort = webServerInitializedEvent.getWebServer().getPort();
        try {
            this.serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (Objects.isNull(this.serverIp)) {
                this.serverIp = "UnknownHost";
            }
        }
    }
}
