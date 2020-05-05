package cn.net.ckia.rabbitmqtest.controller;

import cn.net.ckia.rabbitmqtest.publish.StandardSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class StandardController {

    @Autowired
    private StandardSender standardSender;

    @GetMapping("standard/send")
    public void standardSend(@RequestParam("exchange")  String exchange,@RequestParam("routingKey")  String routingKey,
                             @RequestParam("message") String message,boolean batch){
        if (batch) {
            for (int i = 0; i < 10; i++) {
                standardSender.amqpSend(exchange,routingKey,message);
            }
            return;
        }
        standardSender.amqpSend(exchange,routingKey,message);
    }
    @GetMapping("delayMsg")
    public void delayMsg(String msg, Integer delayTime) {
        LocalDateTime dateTime = LocalDateTime.now();
        msg = "发送开始时间:"+dateTime.getHour()+":"+dateTime.getMinute()+":"+dateTime.getSecond();
        log.info("当前时间：{},收到请求，msg:{},delayTime:{}", dateTime, msg, delayTime);
        standardSender.sendDelayMsg(msg, delayTime);
    }
    @GetMapping("priority")
    public void priority(@RequestParam("priority") Integer priority){
        standardSender.amqpSendPriority("ckia_direct","priority_routingKey_queue","{\"我是优先级队列\":\"我是优先级队列\"}",priority);
    }
}
