package com.alexcode.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

  @NotNull
  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String author;

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
