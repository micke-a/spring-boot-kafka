package me.mikael.springbootkafka.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.springbootkafka.entities.Customer;
import me.mikael.springbootkafka.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer: {}", customer.getName());
        return customerRepository.save(customer);
    }
}