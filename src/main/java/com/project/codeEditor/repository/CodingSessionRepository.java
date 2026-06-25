package com.project.codeEditor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.codeEditor.entity.CodingSession;

public interface CodingSessionRepository extends JpaRepository<CodingSession, Long> {
    Optional<CodingSession> findBySessionLink(String sessionLink);
    boolean existsBySessionLink(String sessionLink);
}
