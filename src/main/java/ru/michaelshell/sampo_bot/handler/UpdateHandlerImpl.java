package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.AUTHENTICATED;


public class UpdateHandlerImpl implements UpdateHandler {

    private final Map<String, UpdateHandler> handlers = new HashMap<>();


    public UpdateHandlerImpl(SendServiceImpl sendService, UserService userService) {
//        this.startHandler = new StartHandler(sendService);
        handlers.put("start", new StartHandler(sendService));
        handlers.put("register", new RegisterHandler(userService));
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();
        if (message != null && message.hasText()) {

            if (session.getAttribute(AUTHENTICATED) == null) {
                handlers.get("register").handleUpdate(update, session);
            }

            String messageText = message.getText();
            switch (messageText) {
                case "/start" -> handlers.get("start").handleUpdate(update, session);

            }

        }


        if (update.hasCallbackQuery()) {

        }

    }
}
