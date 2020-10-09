package com.alexcode.controller;

import java.util.HashMap;
import java.util.Map;

import com.alexcode.domain.Book;
import com.alexcode.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Description:
 *    - @EmbeddedKafka: enable embedded kafka for testing
 *    - @TestPropertySource: override the bootstrap-servers property to embedded kafka brokers
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventsControllerTestIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  private Consumer<Long, String> consumer;

  @BeforeEach
  public void setUp() {
    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker));
    consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new StringDeserializer()).createConsumer();
    embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
  }

  @AfterEach
  public void tearDown() {
    consumer.close();
  }

  @Test
  @Timeout(5)
  public void postLibraryEvent() {
    // given
    Book book = Book.getBuilder()
            .withId(1L)
            .withName("tester")
            .withAuthor("tester")
            .build();

    LibraryEvent libraryEvent = LibraryEvent.getBuilder()
            .withId(1L)
            .withBook(book)
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.set("content-type", MediaType.APPLICATION_JSON.toString());
    HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

    // when
    ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("/v1/library/event", HttpMethod.POST, request, LibraryEvent.class);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    ConsumerRecord<Long, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
    String expectedValue = "{\"id\":1,\"book\":{\"id\":1,\"name\":\"tester\",\"author\":\"tester\"},\"type\":\"NEW\"}";
    String actualValue = consumerRecord.value();
    assertEquals(expectedValue, actualValue);
  }
}
