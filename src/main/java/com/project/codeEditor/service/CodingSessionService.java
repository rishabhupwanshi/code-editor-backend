package com.project.codeEditor.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.codeEditor.dto.CodingSessionResponse;
import com.project.codeEditor.dto.CreateSessionRequest;
import com.project.codeEditor.dto.JoinSessionRequest;
import com.project.codeEditor.entity.CodingSession;
import com.project.codeEditor.repository.CodingSessionRepository;

@Service
public class CodingSessionService {

    @Autowired
    private CodingSessionRepository sessionRepository;

    public CodingSessionResponse createSession(CreateSessionRequest request) {
        CodingSession session = new CodingSession();
        session.setSessionName(request.getHostName());
        session.setProgrammingLanguage(request.getProgrammingLanguage());
        session.setSessionLink(generateUniqueToken());
        Boolean publicRoom = request.getPublicRoom();
        session.setPublicRoom(publicRoom == null ? Boolean.TRUE : publicRoom);
        session.setStatus("ACTIVE");

        CodingSession saved = sessionRepository.save(session);
        return toResponse(saved);
    }

    public CodingSessionResponse joinSession(JoinSessionRequest request) {
        CodingSession session = sessionRepository.findBySessionLink(request.getSessionToken())
                .orElseThrow(() -> new IllegalArgumentException("Session not found for token: " + request.getSessionToken()));
        return toResponse(session);
    }

    public CodingSessionResponse getSessionByToken(String token) {
        CodingSession session = sessionRepository.findBySessionLink(token)
                .orElseThrow(() -> new IllegalArgumentException("Session not found for token: " + token));
        return toResponse(session);
    }

    public List<CodingSessionResponse> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CodingSessionResponse toResponse(CodingSession session) {
        CodingSessionResponse response = new CodingSessionResponse();
        response.setId(session.getId());
        response.setHostName(session.getSessionName());
        response.setProgrammingLanguage(session.getProgrammingLanguage());
        response.setSessionToken(session.getSessionLink());
        response.setPublicRoom(session.getPublicRoom());
        response.setStatus(session.getStatus());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString().replaceAll("[-]", "").substring(0, 8).toUpperCase();
        } while (sessionRepository.existsBySessionLink(token));
        return token;
    }
}
