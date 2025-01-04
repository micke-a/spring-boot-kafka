

# Spring Boot + Kafka testing

Notes about testing various spring boot/kafka bits.

## Things to test out

### Batch processing errors move specific records to DLT


## Things which had to be resolved

### Have both String and Avro values being published
Configure the Delegating By Topic Serde stuff https://docs.spring.io/spring-kafka/reference/kafka/serdes.html#by-topic so I can have both...

### Docker compose configured hostname Unknown

App couldn't resolve the `schemaregistry` hostname, just used localhost.

### MessageConversionException

```
Caused by: org.springframework.messaging.converter.MessageConversionException: Cannot convert from [me.mikael.springbootkafka.generated.v1.Order] to [me.mikael.springbootkafka.generated.v1.Order] for GenericMessage [payload={"product": "Batch 1735991075846 10", "quantity": 100, "price": 10.0}, headers={kafka_offset=4, kafka_consumer=org.springframework.kafka.core.DefaultKafkaConsumerFactory$ExtendedKafkaConsumer@72942c40, kafka_timestampType=CREATE_TIME, kafka_receivedPartitionId=2, kafka_receivedMessageKey=Batch 1735991075846 10, kafka_receivedTopic=me-mikael-kafka-orders-v001, kafka_receivedTimestamp=1735991076588, kafka_groupId=spring-boot-kafka-group}]
```
Found solution here https://github.com/spring-projects/spring-kafka/issues/1665
Caused by Spring DevTools causing class to be loaded by different class loaders.

I removed the devtools from this project
