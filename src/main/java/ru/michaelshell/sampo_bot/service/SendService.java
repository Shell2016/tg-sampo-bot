package ru.michaelshell.sampo_bot.service;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface SendService {

    void send(Long chatId, String msg);

    void sendWithKeyboard(Long chatId, String msg, Session session);

    void sendWithKeyboard(Long chatId, String msg, Session session, InlineKeyboardMarkup inlineKeyboardMarkup);
}
