package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;

@Component
@RequiredArgsConstructor
public class StartHandler implements UpdateHandler {

    private final ResponseSender responseSender;
    private static final String START_MSG = "Привет! Чтобы посмотреть список актуальных колллективок," +
            " нужно тыкнуть на кнопку или ввести команду /events (также доступно через главное меню)";

    @Override
    public void handleUpdate(Request request) {
        Long chatId = request.update().getMessage().getChatId();
        responseSender.sendWithKeyboardBottom(chatId, START_MSG, request.session());
    }
}
