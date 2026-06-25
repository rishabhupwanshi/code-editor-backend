package com.project.codeEditor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.project.codeEditor.dto.CodingSessionResponse;
import com.project.codeEditor.dto.CreateSessionRequest;
import com.project.codeEditor.dto.JoinSessionRequest;
import com.project.codeEditor.service.CodingSessionService;

@RestController
@RequestMapping("/api/sessions")
public class CodingSessionController {

    @Autowired
    private CodingSessionService codingSessionService;

    @PostMapping
    public ResponseEntity<CodingSessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        validateCreateRequest(request);
        CodingSessionResponse response = codingSessionService.createSession(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<CodingSessionResponse> joinSession(@RequestBody JoinSessionRequest request) {
        validateJoinRequest(request);
        try {
            CodingSessionResponse response = codingSessionService.joinSession(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CodingSessionResponse>> getSessions() {
        List<CodingSessionResponse> sessions = codingSessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{token}")
    public ResponseEntity<CodingSessionResponse> getSessionByToken(@PathVariable String token) {
        try {
            CodingSessionResponse response = codingSessionService.getSessionByToken(token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private void validateCreateRequest(CreateSessionRequest request) {
        if (request == null || request.getHostName() == null || request.getHostName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Host name is required.");
        }
        if (request.getProgrammingLanguage() == null || request.getProgrammingLanguage().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Programming language is required.");
        }
    }

    private void validateJoinRequest(JoinSessionRequest request) {
        if (request == null || request.getSessionToken() == null || request.getSessionToken().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session token is required.");
        }
        if (request.getParticipantName() == null || request.getParticipantName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant name is required.");
        }
    }
}
