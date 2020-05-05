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
public class RabbitPriorityConsumer extends RabbitmqConsumer {

    public RabbitPriorityConsumer() {
        super(2, 10000);
    }

    @RabbitHandler
    @RabbitListener(queues = QueueConfig.PRIORITY_QUEUE)
    public void receive(Message message, Channel channel){
        super.processHandler(message,channel);
    }

    /**
     * 业务处理方法
     *
     * @param message
     * @throws Exception
     */
    @Override
    public void businessProcess(Message message) {
        log.info("暂时无业务处理,queue:{},exchange:{}",message.getMessageProperties().getConsumerQueue(),message.getMessageProperties().getReceivedExchange());
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息处理,正常处理完成时调用
     *
     * @param channel
     * @param deliveryTag
     * @throws Exception
     */
    @Override
    public void messageProcess(Channel channel,long deliveryTag) throws Exception {
        channel.basicAck(deliveryTag,false);
    }

    /**
     * 消息处理异常时调用
     *
     * @param channel
     * @param channel
     */
    @Override
    public void messageErrorProcess(Message message, Channel channel) {
        try {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
