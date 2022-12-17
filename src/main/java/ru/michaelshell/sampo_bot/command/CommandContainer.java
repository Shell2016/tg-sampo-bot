package ru.michaelshell.sampo_bot.command;

import ru.michaelshell.sampo_bot.service.SendMessageService;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.command.CommandName.*;

public class CommandContainer {

    private final Map<String, Command> commands;
    private final Command unknownCommand;

    public CommandContainer(SendMessageService sendMessageService) {

        commands = new HashMap<>();
        unknownCommand = new UnknownCommand(sendMessageService);

        commands.put(START.getCommandName(), new StartCommand(sendMessageService));
        commands.put(HELP.getCommandName(), new HelpCommand(sendMessageService));
        commands.put(SETTINGS.getCommandName(), new SettingsCommand(sendMessageService));
        // TODO: 17.12.2022 add more commands
    }

    public Command getCommand(String commandIdentifier) {
        return commands.getOrDefault(commandIdentifier, unknownCommand);
    }
}
