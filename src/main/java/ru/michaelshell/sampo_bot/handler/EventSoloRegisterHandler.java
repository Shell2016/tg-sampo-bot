package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Component
@RequiredArgsConstructor
public class EventSoloRegisterHandler implements CallbackHandler {

    private final UserService userService;
    private final DancerListHandler dancerListHandler;

    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String eventInfo = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto eventGetDto = parseEvent(eventInfo);
        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Ошибка записи!\uD83D\uDE31 Вы уже записаны!")
                    .build());
        }
        try {
            userService.registerOnEvent(eventGetDto, user.getId());
        } catch (DataIntegrityViolationException e) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Ошибка записи!\uD83D\uDE31 Вы уже записаны!")
                    .build());
        } catch (NoSuchElementException e) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Ошибка записи!!\uD83D\uDE31 Введите команду /clear и обновите список коллективок командой /events!")
                    .build());
        }
        List<Response> responseList = new ArrayList<>();
        responseList.add(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .keyboard(AuthUtils.getBottomKeyboard(session))
                .chatId(chatId)
                .message("Успешная запись!\uD83E\uDD73")
                .build());
        responseList.addAll(dancerListHandler.getDancerListWithButtons(eventInfo, user, chatId));
        return responseList;
    }
}
