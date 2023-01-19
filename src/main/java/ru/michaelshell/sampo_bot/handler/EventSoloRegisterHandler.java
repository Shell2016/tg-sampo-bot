package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Slf4j
public class EventSoloRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final UserService userService;


    public EventSoloRegisterHandler(SendServiceImpl sendServiceImpl, UserService userService) {
        this.sendServiceImpl = sendServiceImpl;
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

        EventGetDto eventGetDto = parseEvent(msgText);

        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            sendServiceImpl.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        try {
            userService.registerOnEvent(eventGetDto, user.getId());
        } catch (DataIntegrityViolationException e) {
            sendServiceImpl.edit(chatId, messageId, "Ошибка записи!!\uD83D\uDE31 Вы уже записаны!");
            return;
        }
        log.info("Registration on event " + eventGetDto + " by " + user.getUserName());

        // TODO: 20.01.2023


        sendServiceImpl.edit(chatId, messageId, "Успешная запись!\uD83E\uDD73");
    }


}



