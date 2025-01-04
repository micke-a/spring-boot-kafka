package me.mikael.springbootkafka.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.entities.Customer;
import me.mikael.springbootkafka.generated.v1.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.source-topic-name}")
    private String customerTopicName;

    @Value("${app.kafka.orders-topic-name}")
    private String ordersTopicName;

    @SneakyThrows
    private String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @Transactional("kafkaTransactionManager")
    public void publishCustomers(String batchName, int count) {
        var customers = IntStream.range(0, count)
                .mapToObj(i -> Customer.builder()
                        .name(batchName + " " + i)
                        .birthday(LocalDate.now().minusYears(i))
                        .build()
                )
                .toList();

        var bla = customers.stream().map(customer ->
                        kafkaTemplate.send(
                                customerTopicName,
                                customer.getName(),
                                objectToString(customer)
                        )
                )
                .map(future -> future.whenComplete((sendResult, t) -> {
                            if (t != null) {
                                // handle failure
                                log.info("Unable to send message due to : {}", t.getMessage());
                            } else {
                                // handle success
                                log.info("Sent message with offset={}", sendResult.getRecordMetadata().offset());
                            }
                        })
                ).toList()
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(bla).join();
        log.info("Published {} messages", customers.size());
    }

    @Transactional("kafkaTransactionManager")
    public void publishOrders(String batchName, int count) {
        var orders = IntStream.range(0, count)
                .mapToObj(i ->
                        Order.newBuilder()
                                .setProduct(batchName + " " + i)
                                .setQuantity(i*10)
                                .setPrice(i)
                                .build()
                )
                .toList();

        var bla = orders.stream().map(order ->
                        kafkaTemplate.send(
                                ordersTopicName,
                                order.getProduct().toString(),
                                order
                        )
                )
                .map(future -> future.whenComplete((sendResult, t) -> {
                            if (t != null) {
                                // handle failure
                                log.info("Unable to send message due to : {}", t.getMessage());
                            } else {
                                // handle success
                                log.info("Sent message with offset={}", sendResult.getRecordMetadata().offset());
                            }
                        })
                ).toList()
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(bla).join();
        log.info("Published {} messages", orders.size());
    }


}
