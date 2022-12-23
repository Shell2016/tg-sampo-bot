package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;
import static ru.michaelshell.sampo_bot.util.BotUtils.TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX;
import static ru.michaelshell.sampo_bot.util.BotUtils.isAdmin;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventInfoButtons;

public class EventCreateHandler implements UpdateHandler {

    Map<String, Object> eventParameters = new HashMap<>();

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;


    public EventCreateHandler(SendServiceImpl sendServiceImpl, EventService eventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        if (isAdmin(session)) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
                String eventName = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                eventParameters.clear();
                eventParameters.put("eventName", eventName);
                sendServiceImpl.sendWithKeyboard(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                        "Пример - 25 01 23 20:30", session);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_NAME.name());
                session.setAttribute(EVENT_ADD_WAITING_FOR_DATE.name(), true);
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name()))) {
                String eventDate = message.getText();

                if (validateDate(eventDate)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yy HH:mm");
                    LocalDateTime date = LocalDateTime.parse(eventDate, formatter);
                    eventParameters.put("eventDate", date);
                } else {
                    sendServiceImpl.sendWithKeyboard(chatId, "Неверный формат даты", session);
                    return;
                }
                sendServiceImpl.sendWithKeyboard(chatId, "Нужно краткое описание?", session, eventInfoButtons);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_DATE.name());
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
                String eventInfo = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                eventParameters.put("eventInfo", eventInfo);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_INFO.name());
                EventReadDto event = createEvent(message.getFrom().getUserName());
                onEventSuccessOrFail(session, chatId, event);
                return;
            }
            sendServiceImpl.sendWithKeyboard(chatId, "Введите название/уровень коллективки.\n" +
                    "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_NAME.name(), true);
        }
    }


    private boolean validateDate(String eventDate) {
        return eventDate.matches("([0-2][0-9]|3[0-1]) (0[1-9]|1[0-2]) 2[2-9] ([0-1][0-9]|2[0-3]):[0-5][0-9]");
    }

    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            sendServiceImpl.sendWithKeyboard(chatId, "Введите краткое описание:", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_INFO.name(), true);
        } else if ("buttonInfoNo".equals(callbackData)) {
            EventReadDto event = createEvent(callbackQuery.getFrom().getUserName());
            onEventSuccessOrFail(session, chatId, event);
        }
    }

    private EventReadDto createEvent(String createdBy) {
        EventCreateDto eventDto = EventCreateDto.builder()
                .name((String) eventParameters.get("eventName"))
                .time((LocalDateTime) eventParameters.get("eventDate"))
                .info((String) eventParameters.getOrDefault("eventInfo", ""))
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        return eventService.create(eventDto);
    }

    private void onEventSuccessOrFail(Session session, Long chatId, EventReadDto event) {
        if (event != null) {
            sendServiceImpl.sendWithKeyboard(chatId, "Коллективка успешно добавлена", session);
        } else {
            sendServiceImpl.sendWithKeyboard(chatId, "Не удалось добавить, что-то пошло не так", session);
        }
    }
}
