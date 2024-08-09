package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.session.UserSession;

import java.util.List;

import static ru.michaelshell.sampo_bot.util.AuthUtils.getBottomKeyboard;

@Component
@RequiredArgsConstructor
public class StartHandler implements UpdateHandler {

    private static final String START_MSG = "Привет! Чтобы посмотреть список актуальных колллективок," +
            " нажмите на кнопку внизу или введите команду /events (также доступно через главное меню)";

    @Override
    public List<Response> handleUpdate(Request request) {
        Long chatId = request.update().getMessage().getChatId();
        UserSession session = request.session();

        return List.of(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .chatId(chatId)
                .message(START_MSG)
                .keyboard(getBottomKeyboard(session))
                .build());
    }
}
