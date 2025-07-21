package com.example.demo.repository;

import com.example.demo.model.Writer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WriterRepository extends JpaRepository<Writer, String> {
    Optional<Writer> findByUsername(String username);
}
