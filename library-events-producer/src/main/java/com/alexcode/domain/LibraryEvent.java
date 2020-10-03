package com.alexcode.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LibraryEvent {

  private Long id;

  private Book book;

  private LibraryEventType type;

  public void updateLibraryEventType(LibraryEventType type) {
    this.setType(type);
  }

  public static LibraryEventBuilder getBuilder() {
    return new LibraryEventBuilder();
  }

  private static class LibraryEventBuilder {
    private Long id;
    private Book book;

    public LibraryEventBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public LibraryEventBuilder withBook(Book book) {
      this.book = book;
      return this;
    }

    public LibraryEvent build() {
      LibraryEvent libraryEvent = new LibraryEvent();
      libraryEvent.setId(id);
      libraryEvent.setBook(book);
      return libraryEvent;
    }
  }
}
