package ru.michaelshell.sampo_bot.handler;

import liquibase.pro.packaged.T;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface UpdateHandler {

    void handleUpdate(Update update, Session session);

    void handleCallback(Update update, Session session);
}
