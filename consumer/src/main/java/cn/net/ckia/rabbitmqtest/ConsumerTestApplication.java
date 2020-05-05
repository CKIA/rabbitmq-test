package cn.net.ckia.rabbitmqtest;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class ConsumerTestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConsumerTestApplication.class, args);
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Binding.class);
        Arrays.asList(beanNamesForType).forEach(e -> {
            Object bean = applicationContext.getBean(e);
            System.out.println(bean);
        });
        String[] MessageListenerContainerType = applicationContext.getBeanNamesForType(MessageListenerContainer.class);
        Arrays.asList(MessageListenerContainerType).forEach(e -> {
            Object bean = applicationContext.getBean(e);
            System.out.println(bean);
        });
    }

}
