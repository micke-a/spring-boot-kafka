package me.mikael.springbootkafka.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static me.mikael.springbootkafka.configuration.KafkaConfiguration.TRIAGE_SUFFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class TriageTopicPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> publishToTriage(ConsumerRecord<String, Object> consumerRecord){

        var dltTopicName = consumerRecord.topic() + TRIAGE_SUFFIX;

        return kafkaTemplate.send(dltTopicName, consumerRecord.value());
    }
}
