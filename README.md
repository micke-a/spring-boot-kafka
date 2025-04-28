

# Spring Boot + Kafka testing

Notes about testing various spring boot/kafka bits using

* Kafka
* Schema Registry
* Avro SerDe
* Postgres DB
* Docker Compose
* Spring boot with spring-kafka

## How to get this running

To make sure all code is generated etc. run `mvn clean install -DskipTests`

Just start the `App Compose PGSQL` run configuration within intellij, this should start up the docker containers etc.

Once all is up and running you can start hitting endpoints by using `app.http` which has some things prepared.

## Things to test out

### Non-Transactional Batch processing errors moved specific records to DLT

Look into RetryListener - can be added to FailedBatchProcessor somehow. -> set on DefaultErrorHandler
Look into FailedRecordTracker , used by FailedRecordProcessor


* FailedBatchProcessor.handle; 
  * DefaultErrorHandler.handleBatchAndReturnRemaining(
  * FailedBatchProcessor.doHandle
     * DefaultErrorHandler.handleBatch
       * CommonErrorHandler.handleBatchAndReturnRemaining

### Transactional Batch processing errors moved specific records to DLT
Look into how AfterRollbackProcessor is used for this...

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

I removed the devtools from this project, don't need it, problem solved without custom config.
