package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.SampoBot;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminKeyboard;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListKeyboard;


@Service
@RequiredArgsConstructor
public class SendService {

    private SampoBot sampoBot;

    @Autowired
    public void setSampoBot(SampoBot sampoBot) {
        this.sampoBot = sampoBot;
    }


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


    public void sendWithKeyboard(Long chatId, String msg, Session session) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        if (AuthUtils.isAdmin(session)) {
            sendMessage.setReplyMarkup(eventListAdminKeyboard);
        } else {
            sendMessage.setReplyMarkup(eventListKeyboard);
        }
        try {
            sampoBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendWithKeyboard(Long chatId, String msg, Session session, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            sampoBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void edit(Long chatId, Integer messageId, String msg) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(msg)
                .build();
        try {
            sampoBot.execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void editWithKeyboard(Long chatId, Integer messageId, String msg, InlineKeyboardMarkup inlineButtons) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(msg)
                .parseMode(ParseMode.MARKDOWN)
                .replyMarkup(inlineButtons)
                .build();
        try {
            sampoBot.execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
