package ru.michaelshell.sampo_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.exception.TelegramResponseException;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.dispatcher.RequestDispatcher;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminKeyboard;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListKeyboard;

@Slf4j
@Component
public class SampoBot extends TelegramLongPollingBot implements ResponseSender {

    private final BotProperties botProperties;
    private final UserSessionService sessionService;
    private RequestDispatcher requestDispatcher;

    public SampoBot(BotProperties botProperties, UserSessionService sessionService) {
        super(botProperties.token());
        this.botProperties = botProperties;
        this.sessionService = sessionService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.username();
    }

    @Autowired
    public void setRequestDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null || update.getCallbackQuery() != null) {
            requestDispatcher.dispatchRequest(new Request(update, sessionService.getSession(update)));
        }
    }

    @Override
    public void send(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramResponseException(e);
        }
    }

    @Override
    public void sendWithKeyboardBottom(Long chatId, String msg, UserSession session) {
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
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramResponseException(e);
        }
    }

    @Override
    public void sendWithKeyboardInline(Long chatId, String msg, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramResponseException(e);
        }
    }

    @Override
    public void edit(Long chatId, Integer messageId, String msg) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(msg)
                .build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new TelegramResponseException(e);
        }
    }

    @Override
    public void editWithKeyboardInline(Long chatId, Integer messageId, String msg, InlineKeyboardMarkup inlineButtons) {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(msg)
                .parseMode(ParseMode.MARKDOWN)
                .replyMarkup(inlineButtons)
                .build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new TelegramResponseException(e);
        }
    }
}
