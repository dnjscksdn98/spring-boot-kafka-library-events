package com.alexcode.controller;

import com.alexcode.domain.LibraryEvent;
import com.alexcode.domain.LibraryEventType;
import com.alexcode.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
public class LibraryEventsController {

  @Autowired
  private LibraryEventProducer libraryEventProducer;

  @PostMapping(path = "/v1/library/event")
  public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent)
          throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

    log.info("Before-sendLibraryEvent");
//    libraryEventProducer.sendLibraryEvent(libraryEvent);
//    SendResult<Long, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
//    log.info("SendResult: {}", sendResult.toString());
    libraryEvent.updateLibraryEventType(LibraryEventType.NEW);
    libraryEventProducer.sendLibraryEventWithTopic(libraryEvent);
    log.info("After-sendLibraryEvent");

    return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
  }
}
