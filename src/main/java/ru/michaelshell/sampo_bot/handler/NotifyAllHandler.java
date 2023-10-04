package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.*;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import static ru.michaelshell.sampo_bot.session.State.NOTIFY_ALL;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyAllHandler implements UpdateHandler {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        Long chatId = message.getChatId();
        if (AuthUtils.isAdmin(session)) {
            String msgTxt = message.getText();
            if (session.getState() == NOTIFY_ALL) {
                log.info("Sending message to all users:\n" + msgTxt);
                for (User user : userService.findAll()) {
                    try {
                        responseSender.sendWithKeyboardBottom(user.getId(), msgTxt, session);
                    } catch (Exception e) {
                        log.warn("cannot send message to user: " + user);
                    }
                }
                session.setDefaultState();
                sessionService.updateSession(session);
            } else {
                responseSender.sendWithKeyboardBottom(chatId, "Введите сообщение для отправки всем пользователям:", session);
                session.setState(NOTIFY_ALL);
                sessionService.updateSession(session);
            }
        } else {
            responseSender.sendWithKeyboardBottom(chatId, "Нет прав для данной операции!", session);
        }
    }
}
