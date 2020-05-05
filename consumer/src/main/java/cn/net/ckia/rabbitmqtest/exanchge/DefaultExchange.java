package cn.net.ckia.rabbitmqtest.exanchge;

public interface DefaultExchange {

    String TOPIC_EXCHANGE = "ckia_topic";
    String FANOUT_EXCHANGE = "ckia_fanout";
    String DIRECT_EXCHANGE = "ckia_direct";
    String DIRECT_TTL_EXCHANGE = "ckia_ttl_direct";
    String DELAY_EXCHANGE = "ckia_delay_direct";
}
