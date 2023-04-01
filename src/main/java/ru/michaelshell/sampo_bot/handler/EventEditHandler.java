package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;


@Component
@RequiredArgsConstructor
public class EventEditHandler implements UpdateHandler {

    private final SendService sendService;

    @Override
    public void handleUpdate(Update update, Session session) {
    }

    @Override
    public void handleCallback(Update update, Session session) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String msgText = message.getText();
        Long chatId = message.getChatId();

        sendService.editWithKeyboard(chatId, messageId,  msgText, KeyboardUtils.eventEditButtons);
    }
}





