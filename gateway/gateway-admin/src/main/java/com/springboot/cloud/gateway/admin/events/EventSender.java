package com.springboot.cloud.gateway.admin.events;

import com.springboot.cloud.gateway.admin.config.BusConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class EventSender implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageConverter messageConverter;

    @PostConstruct
    public void init() {
        rabbitTemplate.setMessageConverter(messageConverter);
    }

    public void send(String routingKey, Object object) {
        log.info("routingKey:{}=>message:{}", routingKey, object);
        CorrelationData correlationData =new CorrelationData(((RouteDefinition)object).getId());
        rabbitTemplate.convertAndSend(BusConfig.EXCHANGE_NAME, routingKey, object,correlationData);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b==true){
            //收到rabbitmq应答，更改消息状态为1 已发送
            log.info("发送消息确认成功：{}",b);
        }else {
            //做失败处理，省略
            log.info("发送消息确认失败：{}",b);
        }

    }
}
