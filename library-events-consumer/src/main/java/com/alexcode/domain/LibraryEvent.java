package com.alexcode.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LibraryEvent {

	@Id
	@GeneratedValue
  private Long id;

	@ToString.Exclude
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "libraryEvent", cascade = { CascadeType.ALL })
  private Book book;

  @Enumerated(value = EnumType.STRING)
  private LibraryEventType type;

  public void updateLibraryEventType(LibraryEventType type) {
    this.setType(type);
  }

  public static LibraryEventBuilder getBuilder() {
    return new LibraryEventBuilder();
  }

  public static class LibraryEventBuilder {
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
