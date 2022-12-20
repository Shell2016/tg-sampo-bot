package ru.michaelshell.sampo_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.SampoBot;


@Service
public class SendServiceImpl implements SendService {

    private final SampoBot sampoBot;

    @Autowired
    public SendServiceImpl(SampoBot sampoBot) {
        this.sampoBot = sampoBot;
    }

    @Override
    public void send(Long userId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId)
                .text(message).build();
        try {
            sampoBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
