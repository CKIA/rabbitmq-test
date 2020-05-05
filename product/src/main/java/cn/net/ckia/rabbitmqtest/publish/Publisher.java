package cn.net.ckia.rabbitmqtest.publish;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class Publisher {

    private final boolean transactional = false;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(transactional);
        try {
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            AMQP.BasicProperties properties = builder.expiration("15000").build();
            channel.basicPublish(exchange,routingKey,properties,message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用事务确认方式发送消息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void transactionSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(true);
        try {
            channel.txSelect();
            channel.basicPublish(exchange,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            channel.txCommit();
        } catch (Exception e) {
            try {
                channel.txRollback();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (null != channel) {

                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    /**
     * 使用事务确认方式发送消息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void transactionBatchSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(true);
        try {
            channel.txSelect();
            for (int i = 0; i < 10000; i++) {
                channel.basicPublish(exchange,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,(message+i + "次").getBytes());
            }
            channel.txCommit();
        } catch (Exception e) {
            try {
                channel.txRollback();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (null != channel) {

                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * ConfirmSend模式
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void confirmSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(transactional);
        try {
            channel.confirmSelect();
            for (int i = 0; i < 10000; i++) {
                channel.basicPublish(exchange,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,(message+i + "次").getBytes());
            }
            if (channel.waitForConfirms()) {
                log.info("ConfirmSend success,exchange:{},routingKey:{}",exchange,routingKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null == channel) return;
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * ConfirmBatchSend模式
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void confirmBatchSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(transactional);
        try {
            channel.confirmSelect();
            for (int i = 0; i < 10000; i++) {
                channel.basicPublish(exchange,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,(message+i + "次").getBytes());
            }
            channel.waitForConfirmsOrDie();
            log.info("confirmBatchSend success,exchange:{},routingKey:{}",exchange,routingKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null == channel) return;
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * ConfirmSend异步模式
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void confirmAsynSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(transactional);
        try {
            channel.confirmSelect();
            channel.basicPublish(exchange,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            //异步监听确认和未确认的消息
            channel.addConfirmListener(new TestConfirmListener());
            log.info("confirmBatchSend success,exchange:{},routingKey:{}",exchange,routingKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null == channel) return;
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * ConfirmSend异步模式
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void confirmAsynBatchSend(String exchange,String routingKey,String message){
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(transactional);
        try {
            channel.confirmSelect();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            }
            log.info("confirmAsynBatchSend 耗时:{}",(System.currentTimeMillis() - start));
            //异步监听确认和未确认的消息
            channel.addConfirmListener(new TestConfirmListener());
            log.info("confirmBatchSend success,exchange:{},routingKey:{}",exchange,routingKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null == channel) return;
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void amqpSend(String queueName,Object message){
        amqpTemplate.convertAndSend(queueName,message);
    }
    public void amqpSend(String exchange,String routingKey,String message){
        for (int i = 1; i < 1001; i++) {
            amqpTemplate.convertAndSend(exchange,routingKey,message+i);
        }
    }

    static class TestConfirmListener implements ConfirmListener {
        @Override
        public void handleNack(long deliveryTag, boolean multiple) {
            log.info("未确认消息，标识:{}",+ deliveryTag);
        }
        @Override
        public void handleAck(long deliveryTag, boolean multiple) {
            log.info((String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple)));
        }
    }
}
