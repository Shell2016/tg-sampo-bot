package ru.michaelshell.sampo_bot.handler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Slf4j
public class EventSoloRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;
    private final UserService userService;


    public EventSoloRegisterHandler(SendServiceImpl sendServiceImpl, EventService eventService, UserService userService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;

        this.userService = userService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto event = parseEvent(msgText);

        try {
            userService.registerOnEvent(event, user.getId());
        } catch (DataIntegrityViolationException e) {
            sendServiceImpl.sendWithKeyboard(chatId, "Ошибка записи! Вы уже записаны!", session);
            return;
        }
        log.info("Registration on event " + event + " by " + user.getFirstName() + " " + user.getLastName());
        sendServiceImpl.edit(chatId, messageId, msgText);
        sendServiceImpl.sendWithKeyboard(chatId, "Успешная запись!", session);


    }


}



