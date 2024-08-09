package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dispatcher.RequestDispatcher;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateService {

    private final UserSessionService userSessionService;
    private final RequestDispatcher dispatcher;

    public List<Response> processUpdate(Update update) {
        UserSession session = userSessionService.getSession(update);
        return dispatcher.dispatchRequest(new Request(update, session));
    }
}
