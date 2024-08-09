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
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_ID;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.session.State.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Component
@RequiredArgsConstructor
public class EventCoupleRegisterHandler implements UpdateHandler, CallbackHandler {

    private final EventService eventService;
    private final UserService userService;
    private final DancerListHandler dancerListHandler;
    private final UserSessionService sessionService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        Long chatId = request.update().getMessage().getChatId();
        String name = request.update().getMessage().getText();
        Long userId = request.update().getMessage().getFrom().getId();
        User user = request.update().getMessage().getFrom();

        if (name.split(" ").length != 2) {
            return List.of(Response.builder()
                    .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Неверный формат! Нужно два слова, разделенные одним пробелом.")
                    .build());
        } else {
            String[] s = name.split(" ");
            String partnerFirstName = s[0].trim();
            String partnerLastName = s[1].trim();
            Long eventId = (Long) session.getAttribute(EVENT_ID);
            try {
                userService.registerOnEvent(eventId, userId, partnerFirstName, partnerLastName);
            } catch (DataIntegrityViolationException e) {
                return List.of(Response.builder()
                        .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Ошибка записи! Вы уже записаны!")
                        .build());
            }
            session.removeAttribute(EVENT_ID);
            session.setDefaultState();
            String eventInfo = (String) session.getAttribute(EVENT_INFO);
            session.removeAttribute(EVENT_INFO);
            sessionService.updateSession(session);
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

    @Override
    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText().trim();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        EventGetDto event = parseEvent(msgText);
        if (userService.isAlreadyRegistered(event, user.getId())) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Ошибка записи!\uD83D\uDE31 Вы уже записаны!")
                    .build());
        }
        Long eventId;
        try {
            eventId = eventService.findEventIdByDto(event).orElseThrow();
        } catch (NoSuchElementException e) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Ошибка записи!\uD83D\uDE31 Коллективка удалена либо изменена!\n" +
                            "Обновите список!")
                    .build());
        }
        session.setAttribute(EVENT_ID, eventId);
        session.setState(COUPLE_REGISTER_WAITING_FOR_NAME);
        session.setAttribute(EVENT_INFO, msgText);
        sessionService.updateSession(session);
        return List.of(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .keyboard(AuthUtils.getBottomKeyboard(session))
                .chatId(chatId)
                .message(msgText + "\n\nВведите имя и фамилию партнера/партнерши")
                .build());
    }
}
