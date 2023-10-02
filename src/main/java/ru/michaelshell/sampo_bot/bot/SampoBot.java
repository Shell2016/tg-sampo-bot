package ru.michaelshell.sampo_bot.bot;

import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.dispatcher.RequestDispatcher;

import java.util.Optional;

@Component
public class SampoBot extends TelegramLongPollingSessionBot {

    private final BotProperties botProperties;
    private RequestDispatcher requestDispatcher;

    public SampoBot(BotProperties botProperties) {
        super(botProperties.token());
        this.botProperties = botProperties;
    }

    @Autowired
    public void setUpdateDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public String getBotUsername() {
        return botProperties.username();
    }

    @Override
    public void onUpdateReceived(Update update, Optional<Session> botSession) {
        if (update.getMessage() != null || update.getCallbackQuery() != null) {
            requestDispatcher.dispatchRequest(new Request(update, botSession.orElseThrow()));
        }
    }
}
