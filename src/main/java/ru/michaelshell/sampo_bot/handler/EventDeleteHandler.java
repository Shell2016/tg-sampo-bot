package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Slf4j
public class EventDeleteHandler implements UpdateHandler {


    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;


    public EventDeleteHandler(SendServiceImpl sendServiceImpl, EventService eventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
    }

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
            sendServiceImpl.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
            return;
        }
        if (eventService.delete(event) == 1) {
            log.info("Коллективка удалена");
            sendServiceImpl.edit(chatId, messageId,"Коллективка удалена");
        } else {
            log.info("Ошибка удаления коллективки");
            sendServiceImpl.sendWithKeyboard(chatId, "Ошибка удаления", session);
        }


    }


}
