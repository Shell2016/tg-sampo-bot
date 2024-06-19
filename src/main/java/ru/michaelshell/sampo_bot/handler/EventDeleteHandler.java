package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventDumpService;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Component
@RequiredArgsConstructor
public class EventDeleteHandler implements CallbackHandler {

    private final ResponseSender responseSender;
    private final EventService eventService;
    private final EventDumpService eventDumpService;

    @Override
    public void handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if ("buttonEventDeleteConfirmation".equals(callbackQuery.getData())) {
            try {
                eventDumpService.dumpEvents();
            } catch (RuntimeException e) {
                responseSender.sendWithKeyboardBottom(chatId, "Error: Не удалось выгрузить данные в гугл-таблицу! " + e.getMessage(), session);
            }
            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                responseSender.sendWithKeyboardBottom(chatId, "Не удалось обработать запрос", session);
                return;
            }
            if (eventService.delete(event) == 1) {
                responseSender.edit(chatId, messageId, "Коллективка удалена");
            } else {
                responseSender.edit(chatId, messageId, "Ошибка удаления");
            }

        } else {
            responseSender.editWithKeyboardInline(chatId, messageId, msgText, KeyboardUtils.eventListAdminButtonsDeleteConfirmation);
        }
    }
}
