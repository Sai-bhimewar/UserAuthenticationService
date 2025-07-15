package com.scaler.userauthservice.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaClient {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEmail(String topic,String message){
        kafkaTemplate.send(topic, message);
    }

}
