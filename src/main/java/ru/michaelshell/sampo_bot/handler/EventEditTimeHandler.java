package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventEditTimeHandler implements UpdateHandler, CallbackHandler {

    public static final String EVENT_ID = "eventId";
    private final ResponseSender responseSender;
    private final EventService eventService;
    private final EventEditInfoHandler eventEditInfoHandler;

    @Override
    public void handleUpdate(Request request) {
        Session session = request.session();
        if (AuthUtils.isAdmin(session)) {
            Long eventId = (Long) session.getAttribute(EVENT_ID);
            String msgText = request.update().getMessage().getText();
            Long chatId = request.update().getMessage().getChatId();

            if (TimeParser.isValid(msgText)) {
                LocalDateTime date = TimeParser.parseForEventCreation(msgText);
                if (eventService.updateEventTime(eventId, date).isPresent()) {
                    responseSender.sendWithKeyboardBottom(chatId, "Время обновлено!", session);
                }
            } else {
                responseSender.sendWithKeyboardBottom(chatId, "Неверный формат даты", session);
            }
            session.removeAttribute(EVENT_ID);
            session.removeAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_DATE.name());
        }
    }

    @Override
    public void handleCallback(Request request) {
        Session session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();

        Long eventId = eventEditInfoHandler.getEventId(msgText);
        if (eventId == null) {
            responseSender.edit(chatId, messageId, "Коллективка с данным названием и временем не найдена");
        } else {
            session.setAttribute(EVENT_ID, eventId);
            responseSender.sendWithKeyboardBottom(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                    "Пример - 25 01 23 20:30", session);
            session.setAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_DATE.name(), true);
        }
    }
}
