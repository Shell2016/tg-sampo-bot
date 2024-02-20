package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_ID;
import static ru.michaelshell.sampo_bot.session.State.EVENT_EDIT_WAITING_FOR_NAME;


@Component
@RequiredArgsConstructor
public class EventEditTitleHandler implements UpdateHandler, CallbackHandler {

    private final ResponseSender responseSender;
    private final EventService eventService;
    private final EventEditInfoHandler eventEditInfoHandler;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        if (AuthUtils.isAdmin(session)) {
            Long eventId = (Long) session.getAttribute(EVENT_ID);
            Long chatId = request.update().getMessage().getChatId();
            String msgText = BotUtils.removeUnsupportedChars(request.update().getMessage().getText());
            if (msgText.contains("\n")) {
                responseSender.sendWithKeyboardBottom(chatId, "Недопустим ввод в несколько строк!", session);
            } else {

                if (eventService.updateEventTitle(eventId, msgText).isPresent()) {
                    responseSender.sendWithKeyboardBottom(chatId, "Название обновлено!", session);
                }
            }
            session.removeAttribute(EVENT_ID);
            session.setDefaultState();
            sessionService.updateSession(session);
        }
    }

    @Override
    public void handleCallback(Request request) {
        UserSession session = request.session();
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
            responseSender.sendWithKeyboardBottom(chatId, "Введите название/уровень коллективки.\n" +
                    "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
            session.setState(EVENT_EDIT_WAITING_FOR_NAME);
            sessionService.updateSession(session);
        }
    }
}
