package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.*;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class EventEditTimeHandler implements UpdateHandler {

    private final SendService sendService;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {
        if (AuthUtils.isAdmin(session)) {
            Long eventId =(Long) session.getAttribute("eventId");
            String msgText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (TimeParser.isValid(msgText)) {
                LocalDateTime date = TimeParser.parseForEventCreation(msgText);
                if (eventService.updateEventTime(eventId, date).isPresent()) {
                    sendService.sendWithKeyboardBottom(chatId, "Время обновлено!", session);
                }
            } else {
                sendService.sendWithKeyboardBottom(chatId, "Неверный формат даты", session);
            }
            session.removeAttribute("eventId");
            session.removeAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_DATE.name());


        }
    }

    @Override
    public void handleCallback(Update update, Session session) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();

        EventGetDto eventDto = BotUtils.parseEvent(msgText);
        Long eventId = eventService.findEventIdByDto(eventDto).orElse(null);
        if (eventId == null) {
            sendService.edit(chatId, messageId, "Коллективка с данным названием и временем не найдена");
            return;
        }
        session.setAttribute("eventId", eventId);

        sendService.sendWithKeyboardBottom(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                "Пример - 25 01 23 20:30", session);
        session.setAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_DATE.name(), true);
    }
}





