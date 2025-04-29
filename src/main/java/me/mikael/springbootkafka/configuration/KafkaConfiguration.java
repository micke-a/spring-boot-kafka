package me.mikael.springbootkafka.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    public static final String DLT_SUFFIX = "-dlt";
    public static final String TRIAGE_SUFFIX = "-triage";

    private final KafkaProperties kafkaProperties;
    private final ObservationRegistry observationRegistry;


    // Don't ned to do this manually, works without.
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory){
//        KafkaTemplate<String,Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
//        kafkaTemplate.setObservationEnabled(true);
////        kafkaTemplate.setMicrometerEnabled(false);
//        return kafkaTemplate;
//    }


    @Bean
    public ConsumerFactory<?, ?> kafkaConsumerFactory(
            DefaultKafkaConsumerFactoryCustomizer consumerFactoryCustomizer,
                    KafkaProperties kafkaProperties,
            MeterRegistry meterRegistry){

        var props = kafkaProperties.buildConsumerProperties(null);
        var consumerFactory = new DefaultKafkaConsumerFactory<String, Object>(props);
        consumerFactoryCustomizer.customize(consumerFactory);
        // This needs to be added manually or via customizer above when creating manually
        //consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));

        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Enable observations for the listener
        // Have to put this in, if manually creating this factory (e.g. specify ing error handler like below)
        factory.getContainerProperties().setObservationEnabled(true);
        factory.getContainerProperties().setMicrometerEnabled(true);
        // Set the ObservationRegistry
        // consumer picks up traces from header without this, so not needed by the looks of it
//         factory.getContainerProperties().setObservationRegistry(observationRegistry);

        var errorHandler = new DefaultErrorHandler();
        errorHandler.setRetryListeners(new MyRetryListener());
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public NewTopic sourceTopic(@Value("${app.kafka.source-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic sourceTopicTriage(@Value("${app.kafka.source-topic-name}") String topicName) {
        return TopicBuilder.name(topicName + TRIAGE_SUFFIX)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic sourceTopicDlt(@Value("${app.kafka.source-topic-name}") String topicName) {
        return TopicBuilder.name(topicName + DLT_SUFFIX)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic ordersTopic(@Value("${app.kafka.orders-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic ordersTopicTriage(@Value("${app.kafka.orders-topic-name}") String topicName) {
        return TopicBuilder.name(topicName + TRIAGE_SUFFIX)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic ordersTopicDlt(@Value("${app.kafka.orders-topic-name}") String topicName) {
        return TopicBuilder.name(topicName + DLT_SUFFIX)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    @Slf4j
    public static class MyRetryListener implements RetryListener{
        @Override
        public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {
            log.info("Failed delivery record: {}, attempt: {}", record.toString(), deliveryAttempt);
        }

        @Override
        public void recovered(ConsumerRecord<?, ?> record, Exception ex) {
            log.info("Recovered record: {}", record.toString(), ex);
        }

        @Override
        public void recoveryFailed(ConsumerRecord<?, ?> record, Exception original, Exception failure) {
            log.info("RecoveryFailed record: {}, originalEx: {}, failureEx: {}", record.toString(), original, failure, failure);
        }

        @Override
        public void failedDelivery(ConsumerRecords<?, ?> records, Exception ex, int deliveryAttempt) {
            log.info("Failed delivery records: {}, exception: {}, attempt: {}", records.toString(), ex, deliveryAttempt);
        }

        @Override
        public void recovered(ConsumerRecords<?, ?> records, Exception ex) {
            log.info("Recovered records: {}, exception: {}", records.toString(), ex, ex);
        }

        @Override
        public void recoveryFailed(ConsumerRecords<?, ?> records, Exception original, Exception failure) {
            log.info("Recovery Failed records: {}, originalEx: {}, failureEx: {}", records.toString(), original, failure, failure);
        }
    }
}
