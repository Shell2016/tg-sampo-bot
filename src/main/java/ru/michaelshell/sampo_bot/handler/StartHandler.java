package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

@Component
@RequiredArgsConstructor
public class StartHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;

    private final static String START_MSG = "Привет! Чтобы посмотреть список актуальных колллективок," +
            " нужно тыкнуть на кнопку или ввести команду /events (также доступно через главное меню)";

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        sendServiceImpl.sendWithKeyboard(chatId, START_MSG, session);
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


}
