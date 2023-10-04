package ru.michaelshell.sampo_bot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.michaelshell.sampo_bot.session.UserSession;

public interface ResponseSender {
    void send(Long chatId, String message);
    void sendWithKeyboardBottom(Long chatId, String msg, UserSession session);
    void sendWithKeyboardInline(Long chatId, String msg, InlineKeyboardMarkup inlineKeyboardMarkup);
    void edit(Long chatId, Integer messageId, String msg);
    void editWithKeyboardInline(Long chatId, Integer messageId, String msg, InlineKeyboardMarkup inlineButtons);
}
