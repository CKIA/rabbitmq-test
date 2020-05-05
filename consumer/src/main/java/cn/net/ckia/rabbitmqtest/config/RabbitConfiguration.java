package cn.net.ckia.rabbitmqtest.config;

import cn.net.ckia.rabbitmqtest.exanchge.DefaultExchange;
import cn.net.ckia.rabbitmqtest.queue.QueueConfig;
import cn.net.ckia.rabbitmqtest.queue.QueueDefinition;
import cn.net.ckia.rabbitmqtest.queue.RoutingRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Configuration
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() throws Exception{
        Properties properties = new Properties();
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        properties.load(resource);
        resource.close();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(properties.get("spring.rabbitmq.host").toString(),
                Integer.valueOf(properties.get("spring.rabbitmq.port").toString()));
        connectionFactory.setUsername(properties.get("spring.rabbitmq.username").toString());
        connectionFactory.setPassword(properties.get("spring.rabbitmq.password").toString());
        connectionFactory.setVirtualHost(properties.get("spring.rabbitmq.virtual-host").toString());
        return connectionFactory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(@Qualifier("connectionFactory") ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        /** 开启手动 ack */
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        /** 消费者并发数量 */
        factory.setConcurrentConsumers(10);
        /**  */
        factory.setChannelTransacted(false);
        /** 每个消费者拉取的数据量,可以理解为每个队列最大数量,默认是250个 */
        factory.setPrefetchCount(250);
        return factory;
    }

    @Bean(DefaultExchange.DIRECT_EXCHANGE)
    public DirectExchange directExchange() {
        return new DirectExchange(DefaultExchange.DIRECT_EXCHANGE,true,false);
    }

    @Bean(DefaultExchange.TOPIC_EXCHANGE)
    public Exchange topicExchange() {
        return new TopicExchange(DefaultExchange.TOPIC_EXCHANGE,true,false);
    }

    @Bean(DefaultExchange.FANOUT_EXCHANGE)
    public Exchange fanoutExchange() {
        return new FanoutExchange(DefaultExchange.FANOUT_EXCHANGE,true,false);
    }

    /** 声明队列信息 */
    @Bean(QueueConfig.FIRST_QUEUE)
    public Queue firstQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.FIRST_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DIRECT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueConfig.FIRST_QUEUE).build();
        queue.addRoutingRelationships(relationship);
        RoutingRelationship relationshipForTopic = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.TOPIC_EXCHANGE)
                .exchangeTypes(ExchangeTypes.TOPIC)
                .routingKey("#.queue").build();
        queue.addRoutingRelationships(relationshipForTopic);
        RoutingRelationship relationshipForFanout = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.FANOUT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.FANOUT)
                .routingKey("#.queue").build();
        queue.addRoutingRelationships(relationshipForFanout);
        return queue;
    }

    @Bean(QueueConfig.SECOND_QUEUE)
    public Queue secondQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.SECOND_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.FANOUT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.FANOUT)
                .routingKey("#.queue").build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }

    @Bean(QueueConfig.THIRD_QUEUE)
    public Queue thirdQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.THIRD_QUEUE, true, false, false);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.TOPIC_EXCHANGE)
                .exchangeTypes(ExchangeTypes.TOPIC)
                .routingKey("#.queue").build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }
}
