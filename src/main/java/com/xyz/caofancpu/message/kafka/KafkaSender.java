package com.xyz.caofancpu.message.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @auth caofanCPU
 * @date 2018-08-03
 */
@Component
public class KafkaSender {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
    
    @Autowired
    private KafkaTemplate kafkaTemplate;
    
    public void sendMessage(KafkaMessage message) {
        logger.info("消息发送中...");
        try {
            kafkaTemplate.send(message.getTopic(), message.getData());
        } catch (Exception e) {
            logger.error("消息发送失败，{}", e.getMessage());
        }
        logger.info("消息发送成功");
    }
    
}
