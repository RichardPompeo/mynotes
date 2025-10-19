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

public class CreateNoteCommand implements Command {
    ApiClient api = new ApiClient();

    @Override
    public String getName() {
        return "criar";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
       try {
           String title = event.getOption("titulo").getAsString();
           String content = event.getOption("conteudo").getAsString();

           event.deferReply().queue();

           Note note = new Note();
           note.setTitle(title);
           note.setContent(content);

           Note createdNote = api.createNote(note);

           Date createdAt = createdNote.getCreatedAt();
           LocalDateTime localCreatedAt = LocalDateTime
                .ofInstant(createdAt.toInstant(), ZoneId.systemDefault());

           String formattedCreatedAt = localCreatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

           EmbedBuilder embed = new EmbedBuilder()
                .setTitle(createdNote.getTitle())
                .setDescription(createdNote.getContent())
                .addField("ID", createdNote.getId().toString(), false)
                .setFooter("Criado em " + formattedCreatedAt)
                .setColor(Color.GREEN);

           event.getHook().sendMessage("Nota criada com sucesso!").setEmbeds(embed.build()).queue();
       } catch (Exception e) {
           e.printStackTrace();
           event.getHook().sendMessage("Erro ao criar nota!").queue();
       }
    }
}
