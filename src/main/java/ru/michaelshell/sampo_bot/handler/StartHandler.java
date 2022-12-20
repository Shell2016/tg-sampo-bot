package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.util.Optional;
import java.util.ResourceBundle;


public class StartHandler implements UpdateHandler{

    private final SendServiceImpl sendServiceImpl;

    private final static String START_MSG = ResourceBundle.getBundle("messages").getString("start");

    public StartHandler(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void handleUpdate(Update update, Session session) {


        Long userId = update.getMessage().getFrom().getId();


        sendServiceImpl.send(userId, START_MSG);
    }


}
