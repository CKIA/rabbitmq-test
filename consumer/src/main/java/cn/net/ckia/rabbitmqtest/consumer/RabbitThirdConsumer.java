package cn.net.ckia.rabbitmqtest.consumer;

import cn.net.ckia.rabbitmqtest.exanchge.DefaultExchange;
import cn.net.ckia.rabbitmqtest.queue.QueueConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RabbitThirdConsumer extends RabbitmqConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RabbitThirdConsumer() {
        super(1, 5000);
    }

    @RabbitHandler
    @RabbitListener(queues = QueueConfig.THIRD_QUEUE)
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
        log.info(message.getMessageProperties().getReceivedExchange()+"暂时无业务处理");
        int a= 1/0;
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
     * @param message
     */
    @Override
    public void messageErrorProcess(Message message, Channel channel) {
        try {
            MessageProperties properties = message.getMessageProperties();
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.contentType(properties.getContentType());
            builder.contentEncoding(properties.getContentEncoding());
            channel.basicPublish(DefaultExchange.FANOUT_EXCHANGE,"#.queue",builder.build(),message.getBody());
            channel.basicAck(properties.getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
