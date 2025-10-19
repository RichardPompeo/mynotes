package com.notesbot;

import com.notesbot.commands.ListNotesCommand;
import com.notesbot.commands.CreateNoteCommand;
import com.notesbot.commands.DeleteNoteCommand;
import com.notesbot.commands.EditNoteCommand;
import com.notesbot.commands.Command;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class Bot extends ListenerAdapter {
    private final Map<String, Command> commands = new HashMap<>();

    public static void main(String[] args) {
        Properties properties = new Properties();

        try (InputStream input = Bot.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JDABuilder.createDefault(properties.getProperty("DISCORD_BOT_TOKEN"))
            .addEventListeners(new Bot())
            .build();
    }

    public Bot() {
        registerCommand(new ListNotesCommand());
        registerCommand(new CreateNoteCommand());
        registerCommand(new DeleteNoteCommand());
        registerCommand(new EditNoteCommand());
    }

    private void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready!");

        // Only first time to create Discord commands
        /* event.getJDA().updateCommands().addCommands(
            Commands.slash("listar", "Listar todas as notas"),
            Commands.slash("criar", "Criar uma nova nota")
                .addOption(OptionType.STRING, "titulo", "O título da nota", true)
                .addOption(OptionType.STRING, "conteudo", "O conteúdo da nota", true),
            Commands.slash("deletar", "Delete a note")
                .addOption(OptionType.INTEGER, "id", "ID da nota a ser deletada", true),
            Commands.slash("editar", "Editar uma nota")
                .addOption(OptionType.INTEGER, "id", "ID da nota a ser editada", true)
                .addOption(OptionType.STRING, "titulo", "O novo título da nota", true)
                .addOption(OptionType.STRING, "conteudo", "O novo conteúdo da nota", true)
                ).queue(); */
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        Command command = commands.get(commandName);

        if (command != null) {
            command.execute(event);
        } else {
            event.reply("Esse comando não é conhecido...").queue();
        }
    }
}
