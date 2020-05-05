package cn.net.ckia.rabbitmqtest.consumer;

import cn.net.ckia.rabbitmqtest.queue.QueueConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RabbitFirstConsumer extends RabbitmqConsumer {

    public RabbitFirstConsumer() {
        super(0, 0);
    }

    @RabbitHandler
    @RabbitListener(queues = QueueConfig.FIRST_QUEUE)
    public void receive(Message message, Channel channel){
        super.processHandler(message,channel);
    }

    @Override
    public void businessProcess(Message message) {
        log.info(message.getMessageProperties().getReceivedExchange()+"暂时无业务处理");
    }

    @Override
    public void messageProcess(Channel channel,long deliveryTag) throws Exception {
        channel.basicAck(deliveryTag,false);
    }

    @Override
    public void messageErrorProcess(Message message, Channel channel) {
        try {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
