package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.time.LocalDateTime;

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

    private final ResponseSender responseSender;
    private final EventService eventService;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        if (AuthUtils.isAdmin(session)) {

            Message message = request.update().getMessage();
            Long chatId = message.getChatId();

            if (session.getState() == EVENT_ADD_WAITING_FOR_NAME) {
                String eventName = BotUtils.removeUnsupportedChars(message.getText());
                if (eventName.contains("\n")) {
                    responseSender.sendWithKeyboardBottom(chatId, "Недопустим ввод в несколько строк!\n" +
                            "Введите название ещё раз", session);
                    return;
                }
                responseSender.sendWithKeyboardBottom(chatId, "Введите дату и время проведения в формате 'dd MM yy HH:mm'\n" +
                        "Пример - 25 01 23 20:30", session);
                session.setAttribute(EVENT_NAME, eventName);
                session.setState(EVENT_ADD_WAITING_FOR_DATE);
                sessionService.updateSession(session);
                return;
            }

            if (session.getState() == EVENT_ADD_WAITING_FOR_DATE) {
                String eventDate = message.getText();

                if (TimeParser.isValid(eventDate)) {
                    LocalDateTime date = TimeParser.parseForEventCreation(eventDate);
                    session.setAttribute(EVENT_DATE, date);
                } else {
                    responseSender.sendWithKeyboardBottom(chatId,"Неверный формат даты",session);
                    return;
                }
                responseSender.sendWithKeyboardInline(
                        chatId,
                        "Нужно краткое описание?",
                        eventInfoButtons);

                session.setDefaultState();
                sessionService.updateSession(session);
                return;
            }

            if (session.getState() == EVENT_ADD_WAITING_FOR_INFO) {
                String eventInfo = BotUtils.removeUnsupportedChars(message.getText());
                session.setAttribute(EVENT_INFO, eventInfo);
                session.setState(DEFAULT);
                EventReadDto event = createEvent(message.getFrom().getUserName(), session);
                sendEventCreateResponse(session, chatId, event);
                return;
            }

            responseSender.sendWithKeyboardBottom(chatId, "Введите название/уровень коллективки.\n" +
                    "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
            session.setState(EVENT_ADD_WAITING_FOR_NAME);
            sessionService.updateSession(session);
        }
    }

    @Override
    public void handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        if ("buttonInfoYes".equals(callbackData)) {
            responseSender.sendWithKeyboardBottom(chatId, "Введите краткое описание:", session);
            session.setState(EVENT_ADD_WAITING_FOR_INFO);
            sessionService.updateSession(session);
        } else if ("buttonInfoNo".equals(callbackData)) {
            EventReadDto event = createEvent(callbackQuery.getFrom().getUserName(), session);
            sendEventCreateResponse(session, chatId, event);
        }
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

    private void sendEventCreateResponse(UserSession session, Long chatId, EventReadDto event) {
        if (event != null) {
            responseSender.sendWithKeyboardBottom(chatId, "Коллективка успешно добавлена", session);
        } else {
            responseSender.sendWithKeyboardBottom(chatId, "Не удалось добавить, что-то пошло не так", session);
        }
        session.clearAttributes();
        sessionService.updateSession(session);
    }
}
