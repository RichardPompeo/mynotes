package com.notesbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import com.notesbot.commands.Command;
import com.notesbot.utils.ApiClient;
import com.notesbot.models.Note;

import java.awt.Color;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EditNoteCommand implements Command {
    ApiClient api = new ApiClient();

    @Override
    public String getName() {
        return "editar";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            Long id = event.getOption("id").getAsLong();
            String title = event.getOption("titulo").getAsString();
            String content = event.getOption("conteudo").getAsString();

            event.deferReply().queue();

            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            Note updatedNote = api.updateNote(id, note);

            Date updatedAt = updatedNote.getUpdatedAt();
            LocalDateTime localUpdatedAt = LocalDateTime
                 .ofInstant(updatedAt.toInstant(), ZoneId.systemDefault());

            String formattedUpdatedAt = localUpdatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            EmbedBuilder embed = new EmbedBuilder()
                 .setTitle(updatedNote.getTitle())
                 .setDescription(updatedNote.getContent())
                 .addField("ID", updatedNote.getId().toString(), false)
                 .setFooter("Atualizado em " + formattedUpdatedAt)
                 .setColor(Color.GREEN);

            event.getHook().sendMessage("Nota editada com sucesso.").setEmbeds(embed.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessage("Ocorreu um erro ao editar a nota.").queue();
        }
    }
}
