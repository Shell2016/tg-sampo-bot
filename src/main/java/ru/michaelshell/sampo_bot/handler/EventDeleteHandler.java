package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;


@Component
@RequiredArgsConstructor
public class EventDeleteHandler implements UpdateHandler {

    private final SendService sendService;
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
        if ("buttonEventDeleteConfirmation".equals(callbackQuery.getData())) {

            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                sendService.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
                return;
            }
            if (eventService.delete(event) == 1) {
                sendService.edit(chatId, messageId,"Коллективка удалена");
            } else {
                sendService.edit(chatId, messageId,"Ошибка удаления");
            }

        } else {
            sendService.editWithKeyboard(chatId, messageId, msgText, KeyboardUtils.eventListAdminButtonsDeleteConfirmation);
        }


    }


}
