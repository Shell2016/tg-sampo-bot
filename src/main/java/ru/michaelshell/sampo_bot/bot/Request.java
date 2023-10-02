package ru.michaelshell.sampo_bot.bot;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;

public record Request(Update update, Session session) {
}
