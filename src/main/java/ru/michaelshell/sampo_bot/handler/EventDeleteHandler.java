package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendService;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;


@Component
@RequiredArgsConstructor
public class EventDeleteHandler implements UpdateHandler {

    private final SendService SendService;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {
    }

    @Override
    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto event = parseEvent(msgText);
        if (event.getName() == null || event.getTime() == null) {
            SendService.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
            return;
        }
        if (eventService.delete(event) == 1) {
            SendService.edit(chatId, messageId,"Коллективка удалена");
        } else {
            SendService.sendWithKeyboard(chatId, "Ошибка удаления", session);
        }


    }


}
