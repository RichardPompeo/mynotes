package com.notesbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.awt.Color;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.notesbot.utils.ApiClient;
import com.notesbot.models.Note;

public class ListNotesCommand implements Command {
    ApiClient api = new ApiClient();

    @Override
    public String getName() {
        return "listar";
    }

    private List<Note> listLast3Notes(List<Note> notes) {
        return notes.subList(0, Math.min(notes.size(), 3));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            List<Note> notes = api.listNotes();

            if (notes.size() > 3) {
                notes = listLast3Notes(notes);
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Notas")
                .setDescription("Lista das " + notes.size() + " últimas notas")
                .setColor(Color.BLUE)
                .setFooter("Mynotes");

            for (Note note : notes) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(note.getCreatedAt().toInstant(), ZoneId.systemDefault());
                String createdAtFormatted = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                String updatedAtFormatted = note.getUpdatedAt() != null
                    ? LocalDateTime.ofInstant(note.getUpdatedAt().toInstant(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "N/A";

                embed.addField("Título", note.getTitle(), true);
                embed.addField("Conteúdo", note.getContent(), true);
                embed.addField("Data de criação", createdAtFormatted, true);
                embed.addField("Data de atualização", updatedAtFormatted, true);
                embed.addField("ID", note.getId().toString(), true);
                embed.addField("", "", true);
            }

            event.replyEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.reply("Ocorreu um erro ao retornar as notas!").queue();
            e.printStackTrace();
        }
    }
}
