package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;


@Slf4j
public class EditProfileHandler implements UpdateHandler {

    private final SendServiceImpl sendService;

    public EditProfileHandler(SendServiceImpl sendService) {
        this.sendService = sendService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        Long chatId = update.getMessage().getChatId();

        sendService.sendWithKeyboard(
                chatId,
                "Какую роль предпочитаете в танце?\uD83E\uDDD0\uD83D\uDC83\uD83D\uDD7A",
                session,
                KeyboardUtils.roleSelectButtons);
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }
}





