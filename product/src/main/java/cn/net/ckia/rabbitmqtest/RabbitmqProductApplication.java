package cn.net.ckia.rabbitmqtest;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class RabbitmqProductApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(RabbitmqProductApplication.class, args);
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Queue.class);
        Arrays.asList(beanNamesForType).forEach(e -> {
            System.out.println(e);
        });
    }

}
