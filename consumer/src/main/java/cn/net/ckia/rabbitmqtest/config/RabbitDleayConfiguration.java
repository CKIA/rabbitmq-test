package cn.net.ckia.rabbitmqtest.config;

import cn.net.ckia.rabbitmqtest.exanchge.DefaultExchange;
import cn.net.ckia.rabbitmqtest.queue.QueueConfig;
import cn.net.ckia.rabbitmqtest.queue.QueueDefinition;
import cn.net.ckia.rabbitmqtest.queue.QueueRoutingKeys;
import cn.net.ckia.rabbitmqtest.queue.RoutingRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitDleayConfiguration {

    /**
     * 死信交换机
     * @return
     */
    @Bean(DefaultExchange.DELAY_EXCHANGE)
    public Exchange delayExchange() {
        return new DirectExchange(DefaultExchange.DELAY_EXCHANGE,true,false);
    }

    /**
     * 延迟交换机
     * @return
     */
    @Bean(DefaultExchange.DIRECT_TTL_EXCHANGE)
    public Exchange directTTLExchange() {
        return new DirectExchange(DefaultExchange.DIRECT_TTL_EXCHANGE,true,false);
    }
    /**
     * 延迟队列
     * @return
     */
    @Bean(QueueConfig.FOUR_QUEUE)
    public Queue fourQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.FOUR_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DIRECT_TTL_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueRoutingKeys.DIRECT_TTL_QUEUE).build();
        queue.addArgument("x-dead-letter-exchange",DefaultExchange.DELAY_EXCHANGE);
        queue.addArgument("x-dead-letter-routing-key", QueueRoutingKeys.DIRECT_TTL_QUEUE);
        queue.addArgument("x-message-ttl",15000);
        queue.addRoutingRelationships(relationship);
        return queue;
    }

    /**
     * 实际消费队列
     * @return
     */
    @Bean(QueueConfig.DELAY_QUEUE)
    public Queue delayQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.DELAY_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DELAY_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueRoutingKeys.DIRECT_TTL_QUEUE).build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }
}
