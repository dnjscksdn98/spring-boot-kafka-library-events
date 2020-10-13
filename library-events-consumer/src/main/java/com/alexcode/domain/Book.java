package com.alexcode.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

	@Id
	private Long id;

	private String name;

  private String author;

	@OneToOne(fetch = FetchType.LAZY)
  private LibraryEvent libraryEvent;

  public static BookBuilder getBuilder() {
    return new BookBuilder();
  }

  public static class BookBuilder {
    private Long id;
    private String name;
    private String author;

    public BookBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public BookBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public BookBuilder withAuthor(String author) {
      this.author = author;
      return this;
    }

    public Book build() {
      Book book = new Book();
      book.setId(id);
      book.setName(name);
      book.setAuthor(author);
      return book;
    }
  }
}
