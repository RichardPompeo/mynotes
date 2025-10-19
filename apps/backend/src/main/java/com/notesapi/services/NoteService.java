package com.notesapi.services;

import com.notesapi.models.Note;
import com.notesapi.repository.NoteRepository;
import com.notesapi.websocket.NotesWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Note> getAllNotes() {
        return noteRepository.findAll()
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

                    return noteRepository.save(note);
                })
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }
}
