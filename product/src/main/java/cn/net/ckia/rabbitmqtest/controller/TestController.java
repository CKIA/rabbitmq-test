package cn.net.ckia.rabbitmqtest.controller;

import cn.net.ckia.rabbitmqtest.exanchge.DefaultExchange;
import cn.net.ckia.rabbitmqtest.publish.Publisher;
import cn.net.ckia.rabbitmqtest.queue.QueueRoutingKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private Publisher publisher;

    @GetMapping("/first")
    public void first(){
        publisher.amqpSend(DefaultExchange.DIRECT_EXCHANGE,QueueRoutingKeys.FIRST_QUEUE,"hello rabbit first");
    }
    @GetMapping("/firstTx")
    public void firstTx(){
        // 投递正常
        publisher.transactionSend(DefaultExchange.DIRECT_EXCHANGE,"first_queue","hello rabbit first tx 正常投递");
        // 投递失败
        publisher.transactionSend(DefaultExchange.DIRECT_EXCHANGE,"first.queue","hello rabbit first tx 投递失败");
    }
    @GetMapping("/firstTxBatch")
    public void firstTxBatch(){
        long start = System.currentTimeMillis();
        publisher.transactionBatchSend(DefaultExchange.DIRECT_EXCHANGE,"first_queue","hello rabbit first firstTxBatch");
        System.out.println("firstTxBatch 耗时:"+(System.currentTimeMillis() - start));
    }
    @GetMapping("/firstBatch")
    public void firstBatch(){
        long start = System.currentTimeMillis();
        publisher.amqpSend(DefaultExchange.DIRECT_EXCHANGE, "first_queue", "hello rabbit first firstBatch");
        System.out.println("firstBatch 耗时:"+(System.currentTimeMillis() - start));
    }
    @GetMapping("/firstForConfirmSend")
    public void firstForConfirmSend(){
        long start = System.currentTimeMillis();
        publisher.confirmBatchSend(DefaultExchange.DIRECT_EXCHANGE,"first_queue","hello rabbit first firstTxBatch");
        System.out.println("firstForConfirmSend 耗时:"+(System.currentTimeMillis() - start));
    }
    @GetMapping("/firstBatchForConfirmSend")
    public void firstBatchForConfirmSend(){
        long start = System.currentTimeMillis();
        publisher.confirmSend(DefaultExchange.DIRECT_EXCHANGE, "first_queue", "hello rabbit first firstBatch");

        System.out.println("firstBatchForConfirmSend 耗时:"+(System.currentTimeMillis() - start));
    }
    @GetMapping("/second")
    public void second(){
        publisher.amqpSend(DefaultExchange.FANOUT_EXCHANGE,QueueRoutingKeys.SECOND_QUEUE,"hello rabbit second ");
    }
    @GetMapping("/secondConfirmAsynBatchSend")
    public void secondConfirmAsynBatchSend(){
        long start = System.currentTimeMillis();
        publisher.confirmAsynBatchSend(DefaultExchange.FANOUT_EXCHANGE,QueueRoutingKeys.SECOND_QUEUE,"hello rabbit second");
        System.out.println("firstBatchForConfirmSend 耗时:"+(System.currentTimeMillis() - start));

    }
    @GetMapping("/secondConfirmAsynSend")
    public void secondConfirmAsynSend(){
        publisher.confirmAsynSend(DefaultExchange.FANOUT_EXCHANGE,QueueRoutingKeys.SECOND_QUEUE,"hello rabbit second");
    }
    @GetMapping("/secondConfirmAsynSendBy")
    public void secondConfirmAsynSend(String exchangeName,String routingKey){
        publisher.confirmAsynSend(exchangeName,routingKey,"hello rabbit second "+routingKey+" "+exchangeName);
    }

    @GetMapping("/third")
    public void third(){
        publisher.amqpSend(DefaultExchange.TOPIC_EXCHANGE,QueueRoutingKeys.THIRD_QUEUE,"hello rabbit third");
    }
    @GetMapping("/four")
    public void four(){
        publisher.amqpSend(DefaultExchange.FANOUT_EXCHANGE,QueueRoutingKeys.THIRD_QUEUE,"hello rabbit third");
    }
}
