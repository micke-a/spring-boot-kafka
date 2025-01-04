package me.mikael.springbootkafka.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.entities.Customer;
import me.mikael.springbootkafka.kafka.AppEventPublisher;
import me.mikael.springbootkafka.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppController {

    private final CustomerService customerService;
    private final AppEventPublisher appEventPublisher;

    @GetMapping("/customer/list")
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/customer/create-source-events")
    public ResponseEntity<Void> createCustomers() {
        log.info("Creating batch of customer source events");
        appEventPublisher.publishCustomers("Batch " + System.currentTimeMillis(), 20);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/create-source-events")
    public ResponseEntity<Void> createOrders() {
        log.info("Creating batch order of source events");
        appEventPublisher.publishOrders("Batch " + System.currentTimeMillis(), 20);
        return ResponseEntity.ok().build();
    }
}
