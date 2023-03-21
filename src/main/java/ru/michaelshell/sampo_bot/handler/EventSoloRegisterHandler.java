package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;


@Component
@RequiredArgsConstructor
public class EventSoloRegisterHandler implements UpdateHandler {

    private final SendService sendService;
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
            sendService.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        try {
            userService.registerOnEvent(eventGetDto, user.getId());
        } catch (DataIntegrityViolationException e) {
            sendService.edit(chatId, messageId, "Ошибка записи!!\uD83D\uDE31 Вы уже записаны!");
            return;
        } catch (NoSuchElementException e) {
            sendService.edit(chatId, messageId, "Ошибка записи!!\uD83D\uDE31 Коллективка уже удалена!");
            return;
        }
        sendService.sendWithKeyboard(chatId, "Успешная запись!\uD83E\uDD73", session);
        sendDancerListWithButtons(eventInfo, user, chatId);
    }

    public void sendDancerListWithButtons(String eventInfo, User user, Long chatId) {
        String resultList = dancerListHandler.getDancerList(eventInfo);

        if (userService.isAlreadyRegistered(BotUtils.parseEvent(eventInfo), user.getId())) {
            sendService.sendWithKeyboard(chatId, resultList, deleteRegistrationButton);
        } else {
            sendService.sendWithKeyboard(chatId, resultList, eventRegisterButton);
        }
    }


}



