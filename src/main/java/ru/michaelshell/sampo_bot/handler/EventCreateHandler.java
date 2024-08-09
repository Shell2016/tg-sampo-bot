package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_DATE;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_NAME;
import static ru.michaelshell.sampo_bot.session.State.DEFAULT;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_DATE;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_INFO;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventInfoButtons;

@Component
@RequiredArgsConstructor
public class EventCreateHandler implements UpdateHandler, CallbackHandler {

    private final EventService eventService;
    private final UserSessionService sessionService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        if (AuthUtils.isAdmin(session)) {
            Message message = request.update().getMessage();
            Long chatId = message.getChatId();
            if (session.getState() == EVENT_ADD_WAITING_FOR_NAME) {
                String eventName = BotUtils.removeUnsupportedChars(message.getText());
                if (eventName.contains("\n")) {
                    return List.of(Response.builder()
                            .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                            .keyboard(AuthUtils.getBottomKeyboard(session))
                            .chatId(chatId)
                            .message("Недопустим ввод в несколько строк!\n")
                            .build());
                }
                session.setAttribute(EVENT_NAME, eventName);
                session.setState(EVENT_ADD_WAITING_FOR_DATE);
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                                "Пример - 25 01 23 20:30")
                        .build());
            }

            if (session.getState() == EVENT_ADD_WAITING_FOR_DATE) {
                String eventDate = message.getText();

                if (TimeParser.isValid(eventDate)) {
                    LocalDateTime date = TimeParser.parseForEventCreation(eventDate);
                    session.setAttribute(EVENT_DATE, date);
                } else {
                    return List.of(Response.builder()
                            .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                            .keyboard(AuthUtils.getBottomKeyboard(session))
                            .chatId(chatId)
                            .message("Неверный формат даты")
                            .build());
                }
                session.setDefaultState();
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(eventInfoButtons)
                        .chatId(chatId)
                        .message("Нужно краткое описание?")
                        .build());
            }
            if (session.getState() == EVENT_ADD_WAITING_FOR_INFO) {
                String eventInfo = BotUtils.removeUnsupportedChars(message.getText());
                session.setAttribute(EVENT_INFO, eventInfo);
                session.setState(DEFAULT);
                EventReadDto event = createEvent(message.getFrom().getUserName(), session);
                return List.of(getEventCreateResponse(session, chatId, event));
            }
            session.setState(EVENT_ADD_WAITING_FOR_NAME);
            sessionService.updateSession(session);
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Введите название/уровень коллективки.\n" +
                            "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)")
                    .build());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            session.setState(EVENT_ADD_WAITING_FOR_INFO);
            sessionService.updateSession(session);
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Введите краткое описание:")
                    .build());
        } else if ("buttonInfoNo".equals(callbackData)) {
            EventReadDto event = createEvent(callbackQuery.getFrom().getUserName(), session);
            return List.of(getEventCreateResponse(session, chatId, event));
        }
        return Collections.emptyList();
    }

    private EventReadDto createEvent(String createdBy, UserSession session) {
        if (session.getAttribute(EVENT_INFO) == null) {
            session.setAttribute(EVENT_INFO, "");
        }
        EventCreateDto eventDto = EventCreateDto.builder()
                .name((String) session.getAttribute(EVENT_NAME))
                .time((LocalDateTime) session.getAttribute(EVENT_DATE))
                .info((String) session.getAttribute(EVENT_INFO))
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        return eventService.create(eventDto);
    }

    private Response getEventCreateResponse(UserSession session, Long chatId, EventReadDto event) {
        session.clearAttributes();
        sessionService.updateSession(session);
        if (event != null) {
            return Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Коллективка успешно добавлена")
                    .build();
        } else {
            return Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Не удалось добавить, что-то пошло не так")
                    .build();
        }
    }
}
