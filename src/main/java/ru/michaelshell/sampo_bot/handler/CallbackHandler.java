package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    void handleCallback(Update update, Session session);
}
