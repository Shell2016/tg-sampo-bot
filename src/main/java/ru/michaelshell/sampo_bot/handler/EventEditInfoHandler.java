package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;

@Component
@RequiredArgsConstructor
public class EventEditInfoHandler implements UpdateHandler, CallbackHandler {

    public static final String EVENT_ID = "eventId";
    private final ResponseSender responseSender;
    private final EventService eventService;

    @Override
    public void handleUpdate(Request request) {
        Session session = request.session();
        if (AuthUtils.isAdmin(session)) {
            Long eventId = (Long) session.getAttribute(EVENT_ID);
            String msgText = request.update().getMessage().getText();
            Long chatId = request.update().getMessage().getChatId();

            if (eventService.updateEventInfo(eventId, msgText).isPresent()) {
                responseSender.sendWithKeyboardBottom(chatId, "Доп. информация обновлена!", session);
            }
            session.removeAttribute(EVENT_ID);
            session.removeAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_INFO.name());
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

        Long eventId = getEventId(msgText);
        if (eventId == null) {
            responseSender.edit(chatId, messageId, "Коллективка с данным названием и временем не найдена");
        } else {
            session.setAttribute(EVENT_ID, eventId);
            responseSender.sendWithKeyboardBottom(chatId, "Введите доп.инфо (можно несколько строк):", session);
            session.setAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_INFO.name(), true);
        }
    }

    public Long getEventId(String msgText) {
        EventGetDto eventDto = BotUtils.parseEvent(msgText);
        return eventService.findEventIdByDto(eventDto).orElse(null);
    }
}
