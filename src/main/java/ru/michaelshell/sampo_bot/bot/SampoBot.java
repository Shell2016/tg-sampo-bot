package ru.michaelshell.sampo_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.handler.UpdateHandlerImpl;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampoBot extends TelegramLongPollingSessionBot {


    private final BotProperties botProperties;
    private UpdateHandlerImpl updateHandlerImpl;

    @Autowired
    public void setUpdateHandlerImpl(UpdateHandlerImpl updateHandlerImpl) {
        this.updateHandlerImpl = updateHandlerImpl;
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
            updateHandlerImpl.handleUpdate(update, botSession.orElseThrow());
        }
    }

}
