package cn.net.ckia.rabbitmqtest.publish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class StandardSender implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    public void sendDelayMsg(String msg, Integer delayTime) {
        rabbitTemplate.convertAndSend("delay_plugins_exchange","delay.plugins.routingKey",msg, messageProperties ->{
            messageProperties.getMessageProperties().setDelay(delayTime);
            return messageProperties;
        });
    }

    public void amqpSend(String routingKey,Object message){
        rabbitTemplate.convertAndSend(routingKey,message);
    }

    public void amqpSend(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    public void amqpSendPriority(String exchange,String routingKey,String msg,Integer priority){
        MessageProperties properties = new MessageProperties();
        properties.setPriority(priority);
        properties.setContentEncoding("UTF-8");
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setContentLength(msg.length());
        Message message = new Message(msg.getBytes(),properties);
        rabbitTemplate.send(exchange,routingKey,message);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功");
            return;
        }
        log.error("消息发送失败,case:{},data:{}",cause,null== correlationData ?correlationData:correlationData.toString());

    }
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息被退回,message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",message,replyCode,replyText,exchange,routingKey);
    }

}
