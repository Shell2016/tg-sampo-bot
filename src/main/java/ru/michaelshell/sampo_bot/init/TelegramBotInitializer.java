package ru.michaelshell.sampo_bot.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.michaelshell.sampo_bot.bot.TelegramBot;
import ru.michaelshell.sampo_bot.command.CommandConfig;
import ru.michaelshell.sampo_bot.config.TelegramBotProperties;

@Component
@RequiredArgsConstructor
public class TelegramBotInitializer {

    private final TelegramBotProperties telegramBotProperties;
    private final TelegramBot telegramBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
        telegramBot.execute(new SetMyCommands(
                new CommandConfig().getMenuCommands(),
                new BotCommandScopeDefault(),
                null
        ));


        telegramBot.sendText(Long.valueOf(telegramBotProperties.adminId()), "Bot started!");
    }
}
