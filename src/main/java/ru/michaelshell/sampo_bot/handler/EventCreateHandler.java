package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;
import static ru.michaelshell.sampo_bot.util.BotUtils.TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventInfoButtons;

@Component
@RequiredArgsConstructor
public class EventCreateHandler implements UpdateHandler {

    private final SendService sendService;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {
        if (AuthUtils.isAdmin(session)) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
                String eventName = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                session.setAttribute("eventName", eventName);
                sendService.sendWithKeyboard(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                        "Пример - 25 01 23 20:30", session);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_NAME.name());
                session.setAttribute(EVENT_ADD_WAITING_FOR_DATE.name(), true);
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name()))) {
                String eventDate = message.getText();

                if (TimeParser.isValid(eventDate)) {
                    LocalDateTime date = TimeParser.parseForEventCreation(eventDate);
                    session.setAttribute("eventDate", date);
                } else {
                    sendService.sendWithKeyboard(chatId, "Неверный формат даты", session);
                    return;
                }
                sendService.sendWithKeyboard(chatId, "Нужно краткое описание?", eventInfoButtons);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_DATE.name());
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
                String eventInfo = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                session.setAttribute("eventInfo", eventInfo);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_INFO.name());
                EventReadDto event = createEvent(message.getFrom().getUserName(), session);
                session.removeAttribute("eventInfo");
                onEventSuccessOrFail(session, chatId, event);
                return;
            }
            sendService.sendWithKeyboard(chatId, "Введите название/уровень коллективки.\n" +
                    "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_NAME.name(), true);
        }
    }



    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            sendService.sendWithKeyboard(chatId, "Введите краткое описание:", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_INFO.name(), true);
        } else if ("buttonInfoNo".equals(callbackData)) {
            EventReadDto event = createEvent(callbackQuery.getFrom().getUserName(), session);
            onEventSuccessOrFail(session, chatId, event);
        }
    }

    private EventReadDto createEvent(String createdBy, Session session) {
        if (session.getAttribute("eventInfo") == null) {
            session.setAttribute("eventInfo", "");
        }
        EventCreateDto eventDto = EventCreateDto.builder()
                .name((String) session.getAttribute("eventName"))
                .time((LocalDateTime) session.getAttribute("eventDate"))
                .info((String) session.getAttribute("eventInfo"))
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        return eventService.create(eventDto);
    }

    private void onEventSuccessOrFail(Session session, Long chatId, EventReadDto event) {
        if (event != null) {
            sendService.sendWithKeyboard(chatId, "Коллективка успешно добавлена", session);
        } else {
            sendService.sendWithKeyboard(chatId, "Не удалось добавить, что-то пошло не так", session);
        }
    }
}
