package com.alexcode.controller;

import com.alexcode.domain.Book;
import com.alexcode.domain.LibraryEvent;
import com.alexcode.producer.LibraryEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private LibraryEventProducer libraryEventProducer;

  @Test
  public void postLibraryEvent() throws Exception {
    // given
    Book book = Book.getBuilder()
            .withId(1L)
            .withName("tester")
            .withAuthor("tester")
            .build();

    LibraryEvent libraryEvent = LibraryEvent.getBuilder()
            .withId(null)
            .withBook(book)
            .build();

    String requestBodyJson = objectMapper.writeValueAsString(libraryEvent);

    when(libraryEventProducer.sendLibraryEventWithTopic(isA(LibraryEvent.class))).thenReturn(null);

    // when
    mockMvc.perform(post("/v1/library/event")
            .content(requestBodyJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
  }

  @Test
  public void postLibraryEvent_4xx() throws Exception {
    // given
    Book book = Book.getBuilder()
            .withId(null)
            .withName(null)
            .withAuthor("tester")
            .build();

    LibraryEvent libraryEvent = LibraryEvent.getBuilder()
            .withId(null)
            .withBook(book)
            .build();

    String requestBodyJson = objectMapper.writeValueAsString(libraryEvent);

    String expectedErrorMessage = "book.id - must not be null, book.name - must not be blank";

    when(libraryEventProducer.sendLibraryEventWithTopic(isA(LibraryEvent.class))).thenReturn(null);

    // when
    mockMvc.perform(post("/v1/library/event")
            .content(requestBodyJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andExpect(content().string(expectedErrorMessage));
  }

  @Test
	public void putLibraryEvent() throws Exception {
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

		String requestBodyJson = objectMapper.writeValueAsString(libraryEvent);

		when(libraryEventProducer.sendLibraryEventWithTopic(isA(LibraryEvent.class))).thenReturn(null);

		// when
		mockMvc.perform(put("/v1/library/event")
						.content(requestBodyJson)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
	}

	@Test
	public void putLibraryEvent_4xx() throws Exception {
		// given
		Book book = Book.getBuilder()
						.withId(1L)
						.withName("tester")
						.withAuthor("tester")
						.build();

		LibraryEvent libraryEvent = LibraryEvent.getBuilder()
						.withId(null)
						.withBook(book)
						.build();

		String requestBodyJson = objectMapper.writeValueAsString(libraryEvent);

		when(libraryEventProducer.sendLibraryEventWithTopic(isA(LibraryEvent.class))).thenReturn(null);

		// when
		mockMvc.perform(put("/v1/library/event")
						.content(requestBodyJson)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest());
	}
}
