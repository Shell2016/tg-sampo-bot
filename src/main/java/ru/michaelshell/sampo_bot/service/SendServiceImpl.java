package ru.michaelshell.sampo_bot.service;

import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.SampoBot;

import static ru.michaelshell.sampo_bot.util.BotUtils.isAdmin;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminKeyboard;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListKeyboard;


@Service
public class SendServiceImpl implements SendService {

    private final SampoBot sampoBot;

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
    public void sendWithKeyboard(Long chatId, String msg, Session session) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        if (isAdmin(session)) {
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

    @Override
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

}
