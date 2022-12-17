package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.TelegramBot;

//@Service
@RequiredArgsConstructor
public class SendMessageServiceImpl implements SendMessageService {

    private final TelegramBot telegramBot;

    @Override
    public void sendMessage(Long userId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId)
                .text(message).build();
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
