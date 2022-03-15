package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        String routingKey = "simple";
        //准备消息
        String message = "hello, spring amqp!";
        //消息ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //准备ConfirmCallback
        correlationData.getFuture().addCallback(result -> {
            if (result.isAck()) {
                //ACK
                log.debug("消息成功投递到交换机! 消息ID: {}",correlationData.getId());
            }else{
                //NACK
                log.error("消息投递到交换机失败! 消息ID: {}",correlationData.getId());
                //消息重发
            }
        },ex -> {
            //记录日志
            log.error("消息发送失败!",ex);
            //重发消息
        });
        rabbitTemplate.convertAndSend("camq.topic", routingKey, message,correlationData);
    }


    //消息持久化
    @Test
    public void testDurableMessage(){
        //1. 准备消息
        Message message = MessageBuilder.withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)    //持久化
                .build();
        //2. 发送消息
        rabbitTemplate.convertAndSend("simple.queue",message);
    }



    //惰性队列测试
    @Test
    public void testLazyQueue() throws InterruptedException{
        for (int i = 0; i < 1000000; i++) {
            //1.准备消息
            Message message = MessageBuilder
                    .withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
                    .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                    .build();

            //发送消息
            rabbitTemplate.convertAndSend("lazy,direct",message);
        }
    }

    @Test
    public void testNormalQueue() throws InterruptedException{
        for (int i = 0; i < 1000000; i++) {
            //1.准备消息
            Message message = MessageBuilder
                    .withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
                    .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                    .build();

            //发送消息
            rabbitTemplate.convertAndSend("normal,direct",message);
        }
    }
}
