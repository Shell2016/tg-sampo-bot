package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;

/**
 *  Класс с основной логикой перенаправления запросов в обработчики,
 *  а также создает и хранит их
 */

public class UpdateHandlerImpl implements UpdateHandler {

    private final Map<String, UpdateHandler> handlers = new HashMap<>();

    public UpdateHandlerImpl(SendServiceImpl sendService, UserService userService, EventService eventService, BotProperties botProperties) {
        handlers.put("start", new StartHandler(sendService));
        handlers.put("register", new RegisterHandler(userService));
        handlers.put("promote", new PromotionHandler(sendService, userService, botProperties));
        handlers.put("events", new EventsHandler(sendService, eventService));
        handlers.put("eventAdd", new EventAddHandler(sendService, eventService));
        handlers.put("eventDelete", new EventDeleteHandler(sendService, eventService));
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();


        if (message != null && message.hasText() && message.isUserMessage()) {

            if (session.getAttribute(AUTHENTICATED.name()) == null) {
                handlers.get("register").handleUpdate(update, session);
            }

            waitingStatusMessageRouter(update, session);

            String messageText = message.getText();
            switch (messageText) {
                case "/start", "/help" -> handlers.get("start").handleUpdate(update, session);
                case "/promote" -> handlers.get("promote").handleUpdate(update, session);
                case "/clear" -> session.stop();
                case "/events", "Список коллективок" -> handlers.get("events").handleUpdate(update, session);
                case "Добавить" -> handlers.get("eventAdd").handleUpdate(update, session);

//                default -> handlers.get("default").handleUpdate(update, session);

            }

        }


        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" -> handlers.get("eventAdd").handleCallback(update, session);
                case "buttonEventDelete" -> handlers.get("eventDelete").handleCallback(update, session);
            }

        }

    }

    @Override
    public void handleCallback(Update update, Session session) {

    }

    private void waitingStatusMessageRouter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
            handlers.get("eventAdd").handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name()))) {
            handlers.get("eventAdd").handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
            handlers.get("eventAdd").handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            handlers.get("promote").handleUpdate(update, session);
        }




    }


}
