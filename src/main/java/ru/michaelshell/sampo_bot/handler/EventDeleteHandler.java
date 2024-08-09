package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.service.EventDumpService;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.ArrayList;
import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.EDIT_TEXT_MESSAGE;
import static ru.michaelshell.sampo_bot.model.ResponseType.EDIT_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminButtonsDeleteConfirmation;

@Component
@RequiredArgsConstructor
public class EventDeleteHandler implements CallbackHandler {

    private final EventService eventService;
    private final EventDumpService eventDumpService;

    @Override
    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if ("buttonEventDeleteConfirmation".equals(callbackQuery.getData())) {
            List<Response> responseList = new ArrayList<>();
            try {
                eventDumpService.dumpEvents();
            } catch (RuntimeException e) {
                responseList.add(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Error: Не удалось выгрузить данные в гугл-таблицу! " + e.getMessage())
                        .build());
            }
            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                responseList.add(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Не удалось обработать запрос")
                        .build());
                return responseList;
            }
            if (eventService.delete(event) == 1) {
                responseList.add(Response.builder()
                        .type(EDIT_TEXT_MESSAGE)
                        .messageId(messageId)
                        .message("Коллективка удалена")
                        .chatId(chatId)
                        .build());
                return responseList;
            } else {
                responseList.add(Response.builder()
                        .type(EDIT_TEXT_MESSAGE)
                        .messageId(messageId)
                        .message("Ошибка удаления")
                        .chatId(chatId)
                        .build());
                return responseList;
            }

        } else {
            return List.of(Response.builder()
                    .type(EDIT_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(eventListAdminButtonsDeleteConfirmation)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message(msgText)
                    .build());
        }
    }
}
