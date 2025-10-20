package com.notesapi.repositories;

import com.notesapi.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByDiscordUserId(Long discordUserId);
    Optional<Note> findByIdAndDiscordUserId(Long id, Long discordUserId);
    void deleteByIdAndDiscordUserId(Long id, Long discordUserId);
}
