package ru.michaelshell.sampo_bot.command;

import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.command.CommandName.*;

public class CommandContainer {

    private final Map<String, Command> commands = new HashMap<>();
    private final Command unknownCommand;

    public CommandContainer(SendServiceImpl sendServiceImpl) {

        unknownCommand = new UnknownCommand(sendServiceImpl);

        commands.put(START.getCommandName(), new StartCommand(sendServiceImpl));
        commands.put(HELP.getCommandName(), new HelpCommand(sendServiceImpl));
        commands.put(SETTINGS.getCommandName(), new SettingsCommand(sendServiceImpl));
        // TODO: 17.12.2022 add more commands
    }

    public Command getCommand(String commandIdentifier) {
        return commands.getOrDefault(commandIdentifier, unknownCommand);
    }
}
