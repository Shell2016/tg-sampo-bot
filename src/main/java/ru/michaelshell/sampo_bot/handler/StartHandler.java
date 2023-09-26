package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.bot.SendService;

@Component
@RequiredArgsConstructor
public class StartHandler implements UpdateHandler {

    private final SendService sendService;
    private static final String START_MSG = "Привет! Чтобы посмотреть список актуальных колллективок," +
            " нужно тыкнуть на кнопку или ввести команду /events (также доступно через главное меню)";

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        sendService.sendWithKeyboardBottom(chatId, START_MSG, session);
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


}
