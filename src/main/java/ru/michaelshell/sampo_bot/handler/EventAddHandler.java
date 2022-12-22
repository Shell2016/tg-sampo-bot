package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.session.SessionAttribute;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.keyboard.KeyboardUtils.eventInfoButtons;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;

public class EventAddHandler implements UpdateHandler {

    Map<String, String> eventParameters = new HashMap<>();

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;


    public EventAddHandler(SendServiceImpl sendServiceImpl, EventService eventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        if (session.getAttribute(SessionAttribute.STATUS.name()).equals(Status.ADMIN.name())) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
                String eventName = message.getText();
                eventParameters.put("eventName", eventName);
                sendServiceImpl.sendMessageWithKeyboard(chatId, "Дата и время:", session);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_NAME.name());
                session.setAttribute(EVENT_ADD_WAITING_FOR_DATE.name(), true);
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name()))) {
                String eventDate = message.getText();
                // TODO: 22.12.2022 eventDate validation
                eventParameters.put("eventDate", eventDate);
                sendServiceImpl.sendMessageWithKeyboard(chatId, "Нужно краткое описание?", session, eventInfoButtons);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_DATE.name());
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
                String eventInfo = message.getText();                
                eventParameters.put("eventDate", eventInfo);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_INFO.name());
                addEvent();
                return;
            }

            sendServiceImpl.sendMessageWithKeyboard(chatId, "Название/уровень коллективки:", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_NAME.name(), true);

        }
    }

    public void handleCallback(Update update, Session session) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            sendServiceImpl.sendMessageWithKeyboard(chatId, "Введите краткое описание:", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_INFO.name(), true);
        } else if ("buttonInfoNo".equals(callbackData)) {
                addEvent();
        }
    }
    
    private void addEvent() {
        // TODO: 22.12.2022
        eventParameters.clear();
    }
}
