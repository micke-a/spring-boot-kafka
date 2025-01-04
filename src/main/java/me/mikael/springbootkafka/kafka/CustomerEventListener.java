package me.mikael.springbootkafka.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.entities.Customer;
import me.mikael.springbootkafka.service.CustomerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

    @Transactional("kafkaTransactionManager")
    @KafkaListener(topics = "${app.kafka.source-topic-name}", groupId = "${spring.application.name}-group")
    public void onSourceMessage(String message) {

        try {
            var customer = objectMapper.readValue(message, Customer.class);
            log.info("RECEIVED source event: {}", customer.getName());
            var createdCustomer = customerService.saveCustomer(customer);
            log.info("RECEIVED and SAVED customer: {}", createdCustomer.getName());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
