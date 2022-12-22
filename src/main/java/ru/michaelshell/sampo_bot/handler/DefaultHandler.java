package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.michaelshell.sampo_bot.keyboard.KeyboardUtils;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import static ru.michaelshell.sampo_bot.keyboard.KeyboardUtils.eventListKeyboard;


public class DefaultHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;

    private final static String DEFAULT_MSG = "Привет! Чтобы посмотреть список актуальных колллективок," +
            " нужно тыкнуть на кнопку или ввести команду /events (также доступно через главное меню)";

    public DefaultHandler(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();

        sendServiceImpl.sendMessageWithKeyboard(chatId, DEFAULT_MSG, eventListKeyboard);
    }


}
