package ru.michaelshell.sampo_bot.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.SampoBot;
import ru.michaelshell.sampo_bot.config.CommandConfig;


@RequiredArgsConstructor
public class BotInitializer {

    private final SampoBot sampoBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {

        sampoBot.execute(new SetMyCommands(
                new CommandConfig().getMenuCommands(),
                new BotCommandScopeDefault(),
                null
        ));


        sampoBot.sendText(528357712L, "Bot started!");
    }
}
