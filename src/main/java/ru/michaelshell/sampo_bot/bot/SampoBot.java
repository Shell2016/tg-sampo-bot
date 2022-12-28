package ru.michaelshell.sampo_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.handler.UpdateHandlerImpl;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.Optional;

@Slf4j
@Component
public class SampoBot extends TelegramLongPollingSessionBot {


    private final BotProperties botProperties;
    private final UpdateHandlerImpl updateHandlerImpl;


    public SampoBot(BotProperties botProperties,
                    UserService userService,
                    EventService eventService,
                    UserEventService userEventService) {
        this.botProperties = botProperties;
        this.updateHandlerImpl = new UpdateHandlerImpl(
                new SendServiceImpl(this),
                userService,
                eventService,
                userEventService,
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
        if (update.getMessage() != null || update.getCallbackQuery() != null) {
            updateHandlerImpl.handleUpdate(update, botSession.orElseThrow());
        }
    }

}
