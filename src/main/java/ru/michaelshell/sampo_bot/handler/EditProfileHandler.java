package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditProfileHandler implements UpdateHandler {

    @Override
    public List<Response> handleUpdate(Request request) {
        Long chatId = request.update().getMessage().getChatId();
        return List.of(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .keyboard(KeyboardUtils.roleSelectButtons)
                .chatId(chatId)
                .message("Партнер/Партнёрша?")
                .build());
    }
}





