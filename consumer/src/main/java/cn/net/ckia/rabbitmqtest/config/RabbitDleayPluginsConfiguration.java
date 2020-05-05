package cn.net.ckia.rabbitmqtest.config;

import cn.net.ckia.rabbitmqtest.queue.QueueDefinition;
import cn.net.ckia.rabbitmqtest.queue.RoutingRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitDleayPluginsConfiguration {

    private final String DELAY_PLUGINS_QUEUE = "delay_plugins_queue";
    private final String DELAY_PLUGINS_EXCHANGE = "delay_plugins_exchange";
    private final String DELAY_PLUGINS_ROUTING_KEY = "delay.plugins.routingKey";
    private final String EXCHANGE_TYPE = "x-delayed-message";

    /**
     * 死信交换机
     * @return
     */
    @Bean(DELAY_PLUGINS_EXCHANGE)
    public Exchange delayPluginsExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_PLUGINS_EXCHANGE, EXCHANGE_TYPE, true, false,args);
    }
    /**
     * 实际消费队列
     * @return
     */
    @Bean(DELAY_PLUGINS_QUEUE)
    public Queue delayPluginsQueue () {
        QueueDefinition queue = new QueueDefinition(DELAY_PLUGINS_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DELAY_PLUGINS_EXCHANGE)
                .exchangeTypes("custom")
                .routingKey(DELAY_PLUGINS_ROUTING_KEY).build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }
}
