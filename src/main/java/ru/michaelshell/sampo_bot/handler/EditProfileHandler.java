package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;


@Component
@RequiredArgsConstructor
public class EditProfileHandler implements UpdateHandler {

    private final SendService sendService;

    @Override
    public void handleUpdate(Update update, Session session) {
        Long chatId = update.getMessage().getChatId();

        sendService.sendWithKeyboard(
                chatId,
                "Какую роль предпочитаете в танце?\uD83E\uDDD0\uD83D\uDC83\uD83D\uDD7A",
                KeyboardUtils.roleSelectButtons);
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }
}





