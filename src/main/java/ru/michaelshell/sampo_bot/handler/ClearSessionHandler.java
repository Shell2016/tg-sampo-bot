package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.session.UserSessionService;

@Component
@RequiredArgsConstructor
public class ClearSessionHandler implements UpdateHandler {

    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        sessionService.clearSession(request.session());
    }
}
