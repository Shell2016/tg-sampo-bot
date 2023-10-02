package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;

@Component
@RequiredArgsConstructor
public class EventEditHandler implements CallbackHandler {

    private final ResponseSender responseSender;

    @Override
    public void handleCallback(Request request) {
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();

        responseSender.editWithKeyboardInline(chatId, messageId, msgText, KeyboardUtils.eventEditButtons);
    }
}
