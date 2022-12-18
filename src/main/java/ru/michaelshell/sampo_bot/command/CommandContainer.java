package ru.michaelshell.sampo_bot.command;

import ru.michaelshell.sampo_bot.service.SendService;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.command.CommandName.*;

public class CommandContainer {

    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;

    public CommandContainer(SendService sendService) {

        unknownCommand = new UnknownCommand(sendService);

        commands.put(START.getCommandName(), new StartCommand(sendService));
        commands.put(HELP.getCommandName(), new HelpCommand(sendService));
        commands.put(SETTINGS.getCommandName(), new SettingsCommand(sendService));
        // TODO: 17.12.2022 add more commands
    }

    public Command getCommand(String commandIdentifier) {
        return commands.getOrDefault(commandIdentifier, unknownCommand);
    }
}
