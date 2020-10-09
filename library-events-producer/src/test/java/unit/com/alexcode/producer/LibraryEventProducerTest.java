package com.alexcode.producer;

import java.util.concurrent.ExecutionException;

import com.alexcode.domain.Book;
import com.alexcode.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
public class LibraryEventProducerTest {

  @InjectMocks
  private LibraryEventProducer libraryEventProducer;

  @Mock
  private KafkaTemplate<Long, String> kafkaTemplate;

  @Spy
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void sendLibraryEventWithTopic_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
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

    SettableListenableFuture future = new SettableListenableFuture();
    future.setException(new RuntimeException("Exception calling Kafka"));

    // when
    when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

    // then
    assertThrows(Exception.class, () -> libraryEventProducer.sendLibraryEventWithTopic(libraryEvent).get());
  }

  @Test
  public void sendLibraryEventWithTopic_success() throws JsonProcessingException, ExecutionException, InterruptedException {
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

    SettableListenableFuture future = new SettableListenableFuture();
    Long key = libraryEvent.getId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    ProducerRecord<Long, String> producerRecord = new ProducerRecord<>("library-events", key, value);
    RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1), 1, 1, 342, System.currentTimeMillis(), 1, 2);
    SendResult<Long, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
    future.set(sendResult);

    // when
    when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

    // then
    ListenableFuture<SendResult<Long, String>> listenableFuture = libraryEventProducer.sendLibraryEventWithTopic(libraryEvent);
    SendResult<Long, String> sendResult1 = listenableFuture.get();
    assertEquals(1, sendResult1.getRecordMetadata().partition());
  }
}
