package ru.michaelshell.sampo_bot.handler;

import ru.michaelshell.sampo_bot.bot.Request;

public interface InlineQueryHandler {
    void handleInlineQuery(Request request);
}
