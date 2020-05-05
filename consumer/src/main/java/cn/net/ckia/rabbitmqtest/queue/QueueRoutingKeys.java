package cn.net.ckia.rabbitmqtest.queue;

public interface QueueRoutingKeys {

    String DIRECT_TTL_QUEUE = "ckia_ttl_queue";
    String DIRECT_DELAY_QUEUE = "ckia_delay_queue";
    String PRIORITY_QUEUE = "priority_routingKey_queue";
}
