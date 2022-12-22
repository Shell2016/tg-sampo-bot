package ru.michaelshell.sampo_bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface SendService {

    void send(Long chatId, String msg);

    void sendMessageWithKeyboard(Long chatId, String msg, ReplyKeyboardMarkup keyboard);
}
