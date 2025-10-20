package com.notesapi.controllers;

import com.notesapi.models.Note;
import com.notesapi.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/user/{discordUserId}")
    public ResponseEntity<List<Note>> getUserNotes(@PathVariable Long discordUserId, HttpServletRequest request) {
        Long authDiscordUserId = (Long) request.getAttribute("discordUserId");

        if (authDiscordUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!authDiscordUserId.equals(discordUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Note> notes = noteService.getUserNotes(authDiscordUserId);

        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id, HttpServletRequest request) {
        Long discordUserId = (Long) request.getAttribute("discordUserId");

        if (discordUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Note> noteOpt = noteService.getNoteById(id);

        if (noteOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Note note = noteOpt.get();

        if (!discordUserId.equals(note.getDiscordUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note, HttpServletRequest request) {
        Long discordUserId = (Long) request.getAttribute("discordUserId");

        if (discordUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        note.setDiscordUserId(discordUserId);

        Note createdNote = noteService.createNote(note);

        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note, HttpServletRequest request) {
        Long discordUserId = (Long) request.getAttribute("discordUserId");

        if (discordUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Note> existingOpt = noteService.getNoteById(id);

        if (existingOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Note existing = existingOpt.get();

        if (!discordUserId.equals(existing.getDiscordUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        note.setDiscordUserId(discordUserId);

        Note updatedNote = noteService.updateNote(id, note);

        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, HttpServletRequest request) {
        Long discordUserId = (Long) request.getAttribute("discordUserId");

        if (discordUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Note> existingOpt = noteService.getNoteById(id);

        if (existingOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Note existing = existingOpt.get();

        if (!discordUserId.equals(existing.getDiscordUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        noteService.deleteNote(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
