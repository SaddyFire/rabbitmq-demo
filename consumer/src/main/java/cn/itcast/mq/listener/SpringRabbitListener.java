package cn.itcast.mq.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg) {
        log.debug("消费者接收到simple.queue的消息：【" + msg + "】");
        // System.out.println(1/0);  模拟消费者消费消息失败
        log.info("消息处理成功");
    }
}
