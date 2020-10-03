package com.alexcode.controller;

import com.alexcode.domain.Book;
import com.alexcode.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
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
public class LibraryEventsControllerTest {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
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
  }
}
