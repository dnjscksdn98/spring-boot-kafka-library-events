package com.alexcode.producer;

import com.alexcode.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

  @Autowired
  private KafkaTemplate<Long, String> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Description:
   *    - Send a message to the default topic asynchronously
   *
   */
  public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
    Long key = libraryEvent.getId();
    String value = objectMapper.writeValueAsString(libraryEvent);

    ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
    listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {
      @Override
      public void onFailure(Throwable ex) {
        handleFailure(key, value, ex);
      }

      @Override
      public void onSuccess(SendResult<Long, String> result) {
        handleSuccess(key, value, result);
      }
    });
  }

  /**
   * Description:
   *    - Send a message to the default topic synchronously
   *
   */
  public SendResult<Long, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
    Long key = libraryEvent.getId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    SendResult<Long, String> sendResult = null;

    try {
      sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
    } catch (ExecutionException | InterruptedException e) {
      log.error("ExecutionException / InterruptedException sending the message and exception is {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Exception sending the message and exception is {}", e.getMessage());
      throw e;
    }
    return sendResult;
  }

  /**
   * Description:
   *    - Send a message to an explicit topic asynchronously
   *
   */
  public void sendLibraryEventWithTopic(LibraryEvent libraryEvent) throws JsonProcessingException {
    Long key = libraryEvent.getId();
    String value = objectMapper.writeValueAsString(libraryEvent);

    ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.send("library-events", key, value);
    listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {
      @Override
      public void onFailure(Throwable ex) {
        handleFailure(key, value, ex);
      }

      @Override
      public void onSuccess(SendResult<Long, String> result) {
        handleSuccess(key, value, result);
      }
    });
  }

  private void handleFailure(Long key, String value, Throwable ex) {
    log.error("Error sending the message and exception is {}", ex.getMessage());
    try {
      throw ex;
    } catch (Throwable throwable) {
      log.error("Error in onFailure(): {}", throwable.getMessage());
    }
  }

  private void handleSuccess(Long key, String value, SendResult<Long, String> result) {
    log.info("Message send successfully for the key: {} and the value is {}, partition is {}", key, value, result.getRecordMetadata().partition());
  }
}
