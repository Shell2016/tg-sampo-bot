package ru.michaelshell.sampo_bot.handler;

import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;

import java.util.List;

public interface UpdateHandler {
    List<Response> handleUpdate(Request request);
}
