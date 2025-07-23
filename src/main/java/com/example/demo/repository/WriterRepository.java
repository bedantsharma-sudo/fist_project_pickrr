package com.example.demo.repository;

import com.example.demo.model.Writer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, String> {
    Optional<Writer> findByUsername(String username);
}
