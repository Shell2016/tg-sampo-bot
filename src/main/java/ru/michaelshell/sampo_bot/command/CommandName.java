package ru.michaelshell.sampo_bot.command;

import lombok.Getter;

@Getter
public enum CommandName {
    START("/start"),
    HELP("/help"),
    SETTINGS("/settings");

    private final String commandName;

    private CommandName(String commandName) {
        this.commandName = commandName;
    }
}
