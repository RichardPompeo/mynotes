package com.notesapi.services;

import com.notesapi.models.Note;
import com.notesapi.repositories.NoteRepository;
import com.notesapi.websocket.NotesWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NotesWebSocketHandler notesWebSocketHandler;

    public NoteService(NoteRepository noteRepository, NotesWebSocketHandler notesWebSocketHandler) {
        this.noteRepository = noteRepository;
        this.notesWebSocketHandler = notesWebSocketHandler;
    }

    public List<Note> getUserNotes(Long discordUserId) {
        return noteRepository.findByDiscordUserId(discordUserId)
            .stream()
            .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Note createNote(Note note) {
        Note saved = noteRepository.save(note);

        notesWebSocketHandler.broadcast(saved);

        return saved;
    }

    public Note updateNote(Long id, Note updatedNote) {
        return noteRepository.findById(id)
            .map(note -> {
                note.setTitle(updatedNote.getTitle());
                note.setContent(updatedNote.getContent());

                if (updatedNote.getVisibility() != null) {
                    note.setVisibility(updatedNote.getVisibility());
                }

                if (updatedNote.getAlertAt() != null) {
                    note.setAlertAt(updatedNote.getAlertAt());
                }

                notesWebSocketHandler.broadcast(note);

                return noteRepository.save(note);
            })
            .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    @Transactional
    public void deleteNote(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }

        String principal = String.valueOf(auth.getPrincipal());
        Long discordUserId;

        try {
            discordUserId = Long.parseLong(principal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unauthorized");
        }

        noteRepository.deleteByIdAndDiscordUserId(id, discordUserId);
    }
}
