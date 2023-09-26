package ru.michaelshell.sampo_bot.bot;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface SendService {
    void send(Long chatId, String message);
    void sendWithKeyboardBottom(Long chatId, String msg, Session session);
    void sendWithKeyboardInline(Long chatId, String msg, InlineKeyboardMarkup inlineKeyboardMarkup);
    void edit(Long chatId, Integer messageId, String msg);
    void editWithKeyboardInline(Long chatId, Integer messageId, String msg, InlineKeyboardMarkup inlineButtons);
}
