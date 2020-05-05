package cn.net.ckia.rabbitmqtest.config;

import cn.net.ckia.rabbitmqtest.exanchge.DefaultExchange;
import cn.net.ckia.rabbitmqtest.queue.QueueConfig;
import cn.net.ckia.rabbitmqtest.queue.QueueDefinition;
import cn.net.ckia.rabbitmqtest.queue.QueueRoutingKeys;
import cn.net.ckia.rabbitmqtest.queue.RoutingRelationship;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPriorityConfiguration {

    /**
     * 实际消费队列
     * @return
     */
    @Bean(QueueConfig.PRIORITY_QUEUE)
    public Queue delayQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.PRIORITY_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DIRECT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueRoutingKeys.PRIORITY_QUEUE).build();
        queue.addArgument("x-rnax-priority" , 10);
        queue.addRoutingRelationships(relationship);
        return queue;
    }
}
