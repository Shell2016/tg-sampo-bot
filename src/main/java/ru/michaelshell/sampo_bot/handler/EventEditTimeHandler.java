package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_ID;
import static ru.michaelshell.sampo_bot.session.State.EVENT_EDIT_WAITING_FOR_DATE;

@Component
@RequiredArgsConstructor
public class EventEditTimeHandler implements UpdateHandler, CallbackHandler {

    private final EventService eventService;
    private final EventEditInfoHandler eventEditInfoHandler;
    private final UserSessionService sessionService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        if (AuthUtils.isAdmin(session)) {
            Long eventId = (Long) session.getAttribute(EVENT_ID);
            String msgText = request.update().getMessage().getText();
            Long chatId = request.update().getMessage().getChatId();
            session.removeAttribute(EVENT_ID);
            session.setDefaultState();
            sessionService.updateSession(session);
            if (TimeParser.isValid(msgText)) {
                LocalDateTime date = TimeParser.parseForEventCreation(msgText);
                if (eventService.updateEventTime(eventId, date).isPresent()) {
                    return List.of(Response.builder()
                            .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                            .keyboard(AuthUtils.getBottomKeyboard(session))
                            .chatId(chatId)
                            .message("Время обновлено!")
                            .build());
                }
            } else {
                return List.of(Response.builder()
                        .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Неверный формат даты")
                        .build());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();

        Long eventId = eventEditInfoHandler.getEventId(msgText);
        if (eventId == null) {
            return List.of(Response.builder()
                    .type(ResponseType.EDIT_TEXT_MESSAGE)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message("Коллективка с данным названием и временем не найдена")
                    .build());
        } else {
            session.setAttribute(EVENT_ID, eventId);
            session.setState(EVENT_EDIT_WAITING_FOR_DATE);
            sessionService.updateSession(session);
            return List.of(Response.builder()
                    .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Введите дату и время проведения в формате 'dd MM yy HH:mm'\n")
                    .build());
        }
    }
}
