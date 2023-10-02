package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Component
@RequiredArgsConstructor
public class EventSoloRegisterHandler implements CallbackHandler {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DancerListHandler dancerListHandler;

    public void handleCallback(Request request) {
        Session session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String eventInfo = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto eventGetDto = parseEvent(eventInfo);
        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            responseSender.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }
        try {
            userService.registerOnEvent(eventGetDto, user.getId());
        } catch (DataIntegrityViolationException e) {
            responseSender.edit(chatId, messageId, "Ошибка записи!!\uD83D\uDE31 Вы уже записаны!");
            return;
        } catch (NoSuchElementException e) {
            responseSender.edit(chatId, messageId, "Ошибка записи!!\uD83D\uDE31 Коллективка удалена или изменена!\nОбновите список");
            return;
        }
        responseSender.sendWithKeyboardBottom(chatId, "Успешная запись!\uD83E\uDD73", session);
        dancerListHandler.sendDancerListWithButtons(eventInfo, user, chatId);
    }
}
