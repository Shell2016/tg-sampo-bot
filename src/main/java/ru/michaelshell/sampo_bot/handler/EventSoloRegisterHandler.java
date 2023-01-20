package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSoloRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final UserService userService;
    private final DancerListHandler dancerListHandler;

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String eventInfo = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto eventGetDto = parseEvent(eventInfo);

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
        sendServiceImpl.sendWithKeyboard(chatId, "Успешная запись!\uD83E\uDD73", session);
        sendDancerListWithButtons(eventInfo, user, chatId, session);
    }

    public void sendDancerListWithButtons(String eventInfo, User user, Long chatId, Session session) {
        String resultList = dancerListHandler.getDancerList(eventInfo);

        if (userService.isAlreadyRegistered(BotUtils.parseEvent(eventInfo), user.getId())) {
            sendServiceImpl.sendWithKeyboard(chatId, resultList, session, deleteRegistrationButton);
        } else {
            sendServiceImpl.sendWithKeyboard(chatId, resultList, session, eventRegisterButton);
        }
    }


}



