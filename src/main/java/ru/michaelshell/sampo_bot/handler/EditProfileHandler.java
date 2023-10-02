package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;


@Component
@RequiredArgsConstructor
public class EditProfileHandler implements UpdateHandler {

    private final ResponseSender responseSender;

    @Override
    public void handleUpdate(Request request) {
        Long chatId = request.update().getMessage().getChatId();

        responseSender.sendWithKeyboardInline(
                chatId,
                "Какую роль предпочитаете в танце?\uD83E\uDDD0\uD83D\uDC83\uD83D\uDD7A",
                KeyboardUtils.roleSelectButtons);
    }
}





