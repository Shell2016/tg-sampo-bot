package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;


@Component
@RequiredArgsConstructor
public class EventEditTitleHandler implements UpdateHandler {

    private final SendService sendService;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {
        if (AuthUtils.isAdmin(session)) {
            Long eventId =(Long) session.getAttribute("eventId");
            String msgText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (msgText.contains("\n")) {
                sendService.sendWithKeyboard(chatId, "Недопустим ввод в несколько строк!", session);
            } else {

                if (eventService.updateEventTitle(eventId, msgText).isPresent()) {
                    sendService.sendWithKeyboard(chatId, "Название обновлено!", session);
                }
            }
            session.removeAttribute("eventId");
            session.removeAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_NAME.name());
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

        sendService.sendWithKeyboard(chatId, "Введите название/уровень коллективки.\n" +
                "Максимум 128 символов, всё на одной строке (без Ctrl-Enter)", session);
        session.setAttribute(SessionAttribute.EVENT_EDIT_WAITING_FOR_NAME.name(), true);
    }
}




