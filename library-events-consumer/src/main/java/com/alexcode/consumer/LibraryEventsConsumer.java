package com.alexcode.consumer;

import com.alexcode.service.LibraryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LibraryEventsConsumer {

	@Autowired
	private LibraryEventService libraryEventService;

	@KafkaListener(topics = {"library-events"})
	public void onMessage(ConsumerRecord<Long, String> consumerRecord) throws JsonProcessingException {
		log.info("Consumer Record: {}", consumerRecord);
		libraryEventService.processLibraryEvent(consumerRecord);
	}
}
