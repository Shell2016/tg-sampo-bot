package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;
import static ru.michaelshell.sampo_bot.util.BotUtils.isAuthenticated;

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
        handlers.put("eventList", new EventListHandler(sendService, eventService));
        handlers.put("eventCreate", new EventCreateHandler(sendService, eventService));
        handlers.put("eventDelete", new EventDeleteHandler(sendService, eventService));
        handlers.put("eventRegister", new EventRegisterHandler(sendService, eventService));
        handlers.put("roleSet", new RoleSetHandler(sendService, userService));
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();


        if (message != null && message.hasText() && message.isUserMessage()) {

            if (!isAuthenticated(session)) {
                handlers.get("register").handleUpdate(update, session);
            }

            waitingStatusMessageRouter(update, session);

            String messageText = message.getText();
            switch (messageText) {
                case "/start", "/help" -> handlers.get("start").handleUpdate(update, session);
                case "/promote" -> handlers.get("promote").handleUpdate(update, session);
                case "/clear" -> session.stop();
                case "/events", "Список коллективок" -> handlers.get("eventList").handleUpdate(update, session);
                case "Добавить" -> handlers.get("eventCreate").handleUpdate(update, session);


//                default -> handlers.get("default").handleUpdate(update, session);

            }

        }


        if (update.hasCallbackQuery()) {
            if (!isAuthenticated(session)) {
                handlers.get("register").handleCallback(update, session);
            }
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" -> handlers.get("eventCreate").handleCallback(update, session);
                case "buttonEventDelete" -> handlers.get("eventDelete").handleCallback(update, session);
                case "buttonEventRegister" -> handlers.get("eventRegister").handleCallback(update, session);
                case "buttonLeader", "buttonFollower" -> handlers.get("roleSet").handleCallback(update, session);
            }

        }

    }

    @Override
    public void handleCallback(Update update, Session session) {
    }

    private void waitingStatusMessageRouter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name()))) {
            handlers.get("eventCreate").handleUpdate(update, session);
        }
        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name()))) {
            handlers.get("eventCreate").handleUpdate(update, session);
        }
        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
            handlers.get("eventCreate").handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            handlers.get("promote").handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(SET_ROLE_WAITING_FOR_NAME.name()))) {
            handlers.get("roleSet").handleUpdate(update, session);
        }





    }


}
