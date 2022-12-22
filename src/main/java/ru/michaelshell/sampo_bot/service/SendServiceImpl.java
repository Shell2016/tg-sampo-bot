package ru.michaelshell.sampo_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
    public void send(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message).build();
        try {
            sampoBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageWithKeyboard(Long chatId, String msg, ReplyKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        if (keyboard != null) {
            sendMessage.setReplyMarkup(keyboard);
        }

//        SendMessage sendMessage = SendMessage.builder()
//                .parseMode(ParseMode.MARKDOWN)
//                .chatId(chatId)
//                .text(msg)
//                .replyMarkup(keyboard)
//                .build();

        try {
            sampoBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
