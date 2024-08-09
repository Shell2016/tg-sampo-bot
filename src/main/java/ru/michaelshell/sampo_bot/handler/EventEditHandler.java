package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;

import java.util.List;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventEditButtons;

@Component
@RequiredArgsConstructor
public class EventEditHandler implements CallbackHandler {

    @Override
    public List<Response> handleCallback(Request request) {
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();
        return List.of(Response.builder()
                .type(ResponseType.EDIT_TEXT_MESSAGE_WITH_KEYBOARD)
                .keyboard(eventEditButtons)
                .chatId(chatId)
                .messageId(messageId)
                .message(msgText)
                .build());
    }
}
