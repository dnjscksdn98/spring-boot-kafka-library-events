# spring-boot-kafka-library-events
Distributed Streaming System using Apache Kafka &amp; Spring Boot

## Architecture Structure

<a><img src="https://i.ibb.co/0m4pm53/library-event-domain-1.png" alt="library-event-domain-1" border="0"></a>

## Kafka Producer Configurations
[docs](https://kafka.apache.org/documentation/#producerconfigs)

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
