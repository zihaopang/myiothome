package com.myiothome;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class KafkaTests {
    @Autowired
    KafkaProducer kafkaProducer;
    @Autowired
    KafkaConsumer kafkaConsumer;

    @Test
    public void kafkaTest(){
        kafkaProducer.send("test","你好，");
        kafkaProducer.send("test","在吗？");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer{
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void send(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test"})
    public void receive(ConsumerRecord consumerRecord){
        System.out.println(consumerRecord.value());
    }
}
