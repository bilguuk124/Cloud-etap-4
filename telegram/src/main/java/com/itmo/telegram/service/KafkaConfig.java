package com.itmo.telegram.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Bean
    public Properties kafkaProperties(){
        String host = "rc1a-6qcf7f94uasls9m1.mdb.yandexcloud.net:9092";
        String user = "guest";
        String pass = "Gungun124";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, user, pass);

        String serializer = StringSerializer.class.getName();
        Properties props = new Properties();
        props.put("bootstrap.servers", host);
        props.put("acks", "all");
        props.put("key.serializer", serializer);
        props.put("value.serializer", serializer);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", jaasCfg);
        return  props;
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer(){
        return new KafkaProducer<String, String>(kafkaProperties());
    }
}
