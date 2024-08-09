package ru.michaelshell.sampo_bot.handler;

import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;

import java.util.List;

public interface CallbackHandler {
    List<Response> handleCallback(Request request);
}
