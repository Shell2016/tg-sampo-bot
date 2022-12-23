package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import static ru.michaelshell.sampo_bot.util.BotUtils.hasRole;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

public class EventSoloRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;


    public EventSoloRegisterHandler(SendServiceImpl sendServiceImpl, EventService eventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {

    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();

        if (!hasRole(session)) {
            sendServiceImpl.sendWithKeyboard(chatId, "Для продолжения нужно пройти небольшую регистрацию", session, roleSelectButtons);

        } else {
            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                sendServiceImpl.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
                return;
            }

            sendServiceImpl.sendWithKeyboard(chatId, "Как регистрируемся?", session, registerEventModeButtons);

//            eventService.register(event, callbackQuery.getFrom());

        }






    }


}
