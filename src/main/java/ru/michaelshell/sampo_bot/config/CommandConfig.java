package ru.michaelshell.sampo_bot.config;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфиг для добавления команд, которые появляются в главном меню бота
 */

@Getter
public class CommandConfig {

    private final List<BotCommand> menuCommands;

    public CommandConfig() {
        menuCommands = new ArrayList<>();
        menuCommands.add(new BotCommand("/events", "Список актуальных коллективок"));
//        menuCommands.add(new BotCommand("/help", "some help text"));
//        menuCommands.add(new BotCommand("/settings", "Settings menu"));
    }


}
