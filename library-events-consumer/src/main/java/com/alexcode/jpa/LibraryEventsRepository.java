package com.alexcode.jpa;

import com.alexcode.domain.LibraryEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventsRepository extends JpaRepository<LibraryEvent, Long> {
}
