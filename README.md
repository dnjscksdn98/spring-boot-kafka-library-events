# spring-boot-kafka-library-events
[spring for kafka docs](https://docs.spring.io/spring-kafka/docs/2.6.1/reference/html/#reference)  
Distributed Streaming System using Apache Kafka &amp; Spring Boot

## Architecture Structure

<a><img src="https://i.ibb.co/0m4pm53/library-event-domain-1.png" alt="library-event-domain-1" border="0"></a>

## Kafka Producer Configurations
[kafka producer configuration docs](https://kafka.apache.org/documentation/#producerconfigs)

**acks**

- acks = 1
  - Guarantees message is written to a leader(default)
- acks = all
  - Guarantees message is written to a leader and to all the replicas
- acks = 0
  - No guarantee(Not recommended)

**retries**

- Integer value = [0, 2147483647]
- In Spring Kafka, the default value is 2147483647

**retry.backoff.ms**

- Integer value represented in milliseconds
- Default value is 100ms

## Spring Kafka Consumer

**MessageListenerContainer**

- ```KafkaMessageListenerContainer```
  - Implementation of ```MessageListenerContainer```
  - Polls the records from the kafka topic
  - Committing the offsets after the records are processed
  - Single Threaded
  
- ```ConcurrentMessageListenerContainer```
  - Represents multiple ```KafkaMessageListenerContainer```

**@KafkaListener**

- Uses ```ConcurrentMessageListenerContainer``` behind the scenes
- This is the easiest way to build Kafka Consumer in Spring

- KafkaListener sample code
```java
@KafkaListener(topics = {"${spring.kafka.topic}"})
public void onMessage(ConsumerRecord<Long, String> consumerRecord) {
  log.info("OnMessage Record: {}", consumerRecord);
}
```

- Configuration sample code
```java
@Configuration
@EnableKafka
public class LibraryEventsConsumerConfig {
  ...
}
```
