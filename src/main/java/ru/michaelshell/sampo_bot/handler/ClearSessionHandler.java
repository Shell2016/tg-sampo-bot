package ru.michaelshell.sampo_bot.handler;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.bot.Request;

@Component
public class ClearSessionHandler implements UpdateHandler {
    @Override
    public void handleUpdate(Request request) {
        request.session().stop();
    }
}
