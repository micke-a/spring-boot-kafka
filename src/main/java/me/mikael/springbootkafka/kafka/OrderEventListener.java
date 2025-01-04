package me.mikael.springbootkafka.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.entities.Customer;
import me.mikael.springbootkafka.generated.v1.Order;
import me.mikael.springbootkafka.service.CustomerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {


    @Transactional("kafkaTransactionManager")
    @KafkaListener(
            topics = "${app.kafka.orders-topic-name}",
            groupId = "${spring.application.name}-group")
    public void onSourceMessage(Order order) {
            log.info("RECEIVED ORDER non-batch source event: {}", order.toString());
    }


    @Transactional("kafkaTransactionManager")
    @KafkaListener(
            topics = "${app.kafka.orders-topic-name}",
            groupId = "${spring.application.name}-group-batch",
            batch = "true"
    )
    public void batchConsumer(List<Order> orders) {
        log.info("RECEIVED ORDER batched source event: batchSize:{}", orders.size());
    }
}
