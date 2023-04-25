package ru.michaelshell.sampo_bot.bot;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.handler.UpdateDispatcher;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SampoBot extends TelegramLongPollingSessionBot {


    private final BotProperties botProperties;
    private UpdateDispatcher updateDispatcher;

    @Autowired
    public void setUpdateDispatcher(UpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public String getBotUsername() {
        return botProperties.username();
    }

    @Override
    public String getBotToken() {
        return botProperties.token();
    }


    @Override
    public void onUpdateReceived(Update update, Optional<Session> botSession) {
        if (update.getMessage() != null || update.getCallbackQuery() != null) {
            updateDispatcher.handleUpdate(update, botSession.orElseThrow());
        }
    }

}
