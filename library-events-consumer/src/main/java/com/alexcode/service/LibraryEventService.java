package com.alexcode.service;

import java.util.Optional;

import com.alexcode.domain.LibraryEvent;
import com.alexcode.jpa.LibraryEventsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LibraryEventService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LibraryEventsRepository libraryEventsRepository;

	public void processLibraryEvent(ConsumerRecord<Long, String> consumerRecord) throws JsonProcessingException {
		LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
		log.info("Processing Library Event: {}", libraryEvent);

		switch (libraryEvent.getType()) {
			case NEW:
				save(libraryEvent);
				break;
			case UPDATE:
				validate(libraryEvent);
				save(libraryEvent);
				break;
			default:
				log.info("Invalid Library Event type");
		}
	}

	private void validate(LibraryEvent libraryEvent) {
		if (libraryEvent.getId() == null) {
			throw new IllegalArgumentException("Library Event ID is missing");
		}
		Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getId());
		if (!libraryEventOptional.isPresent()) {
			throw new IllegalArgumentException("Not a valid Library Event ID");
		}
		log.info("Validation is successful for the Library Event: {}", libraryEventOptional.get());
	}

	private void save(LibraryEvent libraryEvent) {
		libraryEvent.getBook().setLibraryEvent(libraryEvent);
		libraryEventsRepository.save(libraryEvent);
		log.info("Successfully persisted the Library Event: {}", libraryEvent);
	}
}
