package me.mikael.springbootkafka.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.generated.v1.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
//@Component
@RequiredArgsConstructor
public class TransactionalOrderEventListener {

    @Transactional("kafkaTransactionManager")
    @KafkaListener(
            topics = "${app.kafka.orders-topic-name}",
            groupId = "${spring.application.name}-group")
    public void onSourceMessage(Order order) {
            log.info("RECEIVED ORDER non-batch source event: {}", order.toString());
    }
}
