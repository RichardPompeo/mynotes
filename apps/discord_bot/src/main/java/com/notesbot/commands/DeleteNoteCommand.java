package com.notesbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import com.notesbot.commands.Command;
import com.notesbot.utils.ApiClient;

public class DeleteNoteCommand implements Command {
    ApiClient api = new ApiClient();

    @Override
    public String getName() {
        return "deletar";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            Long id = event.getOption("id").getAsLong();

            event.deferReply().queue();

            Boolean result = api.deleteNote(id);

            if (result) {
                event.getHook().sendMessage("Nota deletada com sucesso!").queue();
            } else {
                event.getHook().sendMessage("ID inválido!").queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessage("Ocorreu um erro ao deletar a nota!").queue();
        }
    }
}
