package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;
import static ru.michaelshell.sampo_bot.util.BotUtils.TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventInfoButtons;

@Component
@RequiredArgsConstructor
public class EventCreateHandler implements UpdateHandler, CallbackHandler {

    public static final String EVENT_INFO = "eventInfo";

    private final ResponseSender responseSender;
    private final EventService eventService;

    @Override
    public void handleUpdate(Request request) {
        Session session = request.session();
        if (AuthUtils.isAdmin(session)) {

            Message message = request.update().getMessage();
            Long chatId = message.getChatId();

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
                String eventName = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                if (eventName.contains("\n")) {
                    responseSender.sendWithKeyboardBottom(chatId, "Недопустим ввод в несколько строк!\n" +
                            "Введите название ещё раз", session);
                    return;
                }
                session.setAttribute("eventName", eventName);
                responseSender.sendWithKeyboardBottom(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
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
                    responseSender.sendWithKeyboardBottom(chatId, "Неверный формат даты", session);
                    return;
                }
                responseSender.sendWithKeyboardInline(chatId, "Нужно краткое описание?", eventInfoButtons);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_DATE.name());
                return;
            }

            if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
                String eventInfo = message.getText().trim().replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ");
                session.setAttribute(EVENT_INFO, eventInfo);
                session.removeAttribute(EVENT_ADD_WAITING_FOR_INFO.name());
                EventReadDto event = createEvent(message.getFrom().getUserName(), session);
                session.removeAttribute(EVENT_INFO);
                onEventSuccessOrFail(session, chatId, event);
                return;
            }
            responseSender.sendWithKeyboardBottom(chatId, "Введите название/уровень коллективки.\n" +
                    "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_NAME.name(), true);
        }
    }

    @Override
    public void handleCallback(Request request) {
        Session session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            responseSender.sendWithKeyboardBottom(chatId, "Введите краткое описание:", session);
            session.setAttribute(EVENT_ADD_WAITING_FOR_INFO.name(), true);
        } else if ("buttonInfoNo".equals(callbackData)) {
            EventReadDto event = createEvent(callbackQuery.getFrom().getUserName(), session);
            onEventSuccessOrFail(session, chatId, event);
        }
    }

    private EventReadDto createEvent(String createdBy, Session session) {
        if (session.getAttribute(EVENT_INFO) == null) {
            session.setAttribute(EVENT_INFO, "");
        }
        EventCreateDto eventDto = EventCreateDto.builder()
                .name((String) session.getAttribute("eventName"))
                .time((LocalDateTime) session.getAttribute("eventDate"))
                .info((String) session.getAttribute(EVENT_INFO))
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        return eventService.create(eventDto);
    }

    private void onEventSuccessOrFail(Session session, Long chatId, EventReadDto event) {
        if (event != null) {
            responseSender.sendWithKeyboardBottom(chatId, "Коллективка успешно добавлена", session);
        } else {
            responseSender.sendWithKeyboardBottom(chatId, "Не удалось добавить, что-то пошло не так", session);
        }
    }
}
