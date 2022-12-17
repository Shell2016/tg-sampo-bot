package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.config.TelegramBotProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties telegramBotProperties;

    private boolean isScreaming = false;


    @Override
    public String getBotUsername() {
        return telegramBotProperties.username();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.token();
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        Long userId = user.getId();
        log.info("Received message: " + msg);

        if (msg.isCommand()) {
            if (msg.getText().equals("/scream")) {
                isScreaming = true;
            } else if (msg.getText().equals("/whisper")) {
                isScreaming = false;
            }
        }

        if (isScreaming) {
            scream(userId, msg);
        } else {
            copyMessage(userId, msg.getMessageId());
        }

    }

    private void scream(Long userId, Message msg) {
        if (msg.hasText()) {
            sendText(userId, msg.getText().toUpperCase());
        } else {
            copyMessage(userId, msg.getMessageId());
        }
    }


    public void sendText(Long userId, String msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId) //Who are we sending a message to
                .text(msg).build();    //Message content
        try {
            execute(sendMessage);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void copyMessage(Long userId, Integer msgId) {
        CopyMessage copyMessage = CopyMessage.builder()
                .fromChatId(userId)
                .chatId(userId)
                .messageId(msgId)
                .build();
        try {
            execute(copyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
