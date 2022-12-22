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

import static ru.michaelshell.sampo_bot.session.SessionAttribute.AUTHENTICATED;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.PROMOTION_WAITING_FOR_USERNAME;

/**
 *  Класс с основной логикой перенаправления запросов в обработчики,
 *  а также для хранения и создания обработчиков запросов *
 */

public class UpdateHandlerImpl implements UpdateHandler {

    private final Map<String, UpdateHandler> handlers = new HashMap<>();


    public UpdateHandlerImpl(SendServiceImpl sendService, UserService userService, EventService eventService, BotProperties botProperties) {
        handlers.put("start", new StartHandler(sendService));
        handlers.put("register", new RegisterHandler(userService));
        handlers.put("promote", new PromotionHandler(sendService, userService, botProperties));
        handlers.put("events", new EventsHandler(sendService, eventService));
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();
        if (!message.isUserMessage()) {
            return;
        }

        if (message != null && message.hasText()) {

            if (session.getAttribute(AUTHENTICATED.name()) == null) {
                handlers.get("register").handleUpdate(update, session);
            }

            messageFilter(update, session);

            String messageText = message.getText();
            switch (messageText) {
                case "/start", "/help" -> handlers.get("start").handleUpdate(update, session);
                case "/promote" -> handlers.get("promote").handleUpdate(update, session);
                case "/clear" -> session.stop();
                case "/events", "Список коллективок" -> handlers.get("events").handleUpdate(update, session);

//                default -> handlers.get("default").handleUpdate(update, session);

            }

        }


        if (update.hasCallbackQuery()) {
            // TODO: 21.12.2022

        }

    }

    private void messageFilter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            handlers.get("promote").handleUpdate(update, session);
        }

    }


}
