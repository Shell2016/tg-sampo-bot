package ru.michaelshell.sampo_bot.service;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface SendService {

    void send(Long chatId, String msg);

    void sendMessageWithKeyboard(Long chatId, String msg, Session session);

    void sendMessageWithKeyboard(Long chatId, String msg, Session session, InlineKeyboardMarkup inlineKeyboardMarkup);
}
