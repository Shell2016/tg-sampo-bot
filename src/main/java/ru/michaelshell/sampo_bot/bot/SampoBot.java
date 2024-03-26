package ru.michaelshell.sampo_bot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.dispatcher.RequestDispatcher;
import ru.michaelshell.sampo_bot.session.UserSessionService;

@Component
public class SampoBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    @Autowired
    private UserSessionService sessionService;
    @Autowired
    private RequestDispatcher requestDispatcher;

    public SampoBot(BotProperties botProperties) {
        super(botProperties.token());
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.username();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null || update.getCallbackQuery() != null || update.hasInlineQuery()) {
            requestDispatcher.dispatchRequest(new Request(update, sessionService.getSession(update)));
        }
    }
}
