package cn.net.ckia.rabbitmqtest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class ReturnCallbackImpl /*implements RabbitTemplate.ReturnCallback*/ {

//    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",message,replyCode,replyText,exchange,routingKey);
    }

}
