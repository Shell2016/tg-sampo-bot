package ru.michaelshell.sampo_bot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.session.UserSession;

public record Request(Update update, UserSession session) {
}
