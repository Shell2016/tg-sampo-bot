package ru.michaelshell.sampo_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.handler.UpdateHandlerImpl;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.Optional;

@Slf4j
@Component
public class SampoBot extends TelegramLongPollingSessionBot {


    private final BotProperties botProperties;
    private final UpdateHandlerImpl updateHandlerImpl;


    public SampoBot(BotProperties botProperties, UserService userService, EventService eventService) {
        this.botProperties = botProperties;
        this.updateHandlerImpl = new UpdateHandlerImpl(
                new SendServiceImpl(this),
                userService,
                eventService,
                botProperties);
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
        updateHandlerImpl.handleUpdate(update, botSession.orElseThrow());
    }


    public void sendText(Long userId, String msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId)
                .text(msg).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
