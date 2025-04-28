package me.mikael.springbootkafka.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.generated.v1.Order;
import me.mikael.springbootkafka.tracing.TracingSupport;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventBatchListener {

    private final TracingSupport tracingSupport;
    @KafkaListener(
            topics = "${app.kafka.orders-topic-name}",
            groupId = "${spring.application.name}-group-batch",
            batch = "true"
    )
    public void batchConsumer(ConsumerRecords<String, Order> consumerRecords) {
        log.info("RECEIVED ORDER batched source event. batchSize:{}", consumerRecords.count());
        for(ConsumerRecord<String, Order> consumerRecord: consumerRecords){
            var headers = consumerRecord.headers();
            handleRecord(consumerRecord);

        }
    }

    private void handleRecord(ConsumerRecord<String, Order> consumerRecord) {
        Order o = consumerRecord.value();
        log.info("RECEIVED ORDER batched source event: {}, headers: {}", o.toString(), consumerRecord.headers());
        if(o.getPrice() % 4 == 0) {
            // A special exception which org.springframework.kafka.listener.FailedBatchProcessor.handle use to determine what to do
            log.info("Simulating error for record: {}", consumerRecord.toString());
            throw new BatchListenerFailedException("Simulated error", consumerRecord);
        }
    }
}
