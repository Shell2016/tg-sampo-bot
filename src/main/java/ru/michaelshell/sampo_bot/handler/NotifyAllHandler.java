package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.NOTIFY_ALL;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyAllHandler implements UpdateHandler {

    private final SendService sendService;
    private final UserService userService;

    @Override
    public void handleUpdate(Update update, Session session) {
        if (AuthUtils.isAdmin(session)) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String msgTxt = message.getText();

            if (Boolean.TRUE.equals((session.getAttribute(NOTIFY_ALL.name())))) {
                log.info("Sending message to all users:\n" + msgTxt);
                for (User user : userService.findAll()) {
                    try {
                        sendService.sendWithKeyboard(user.getId(), msgTxt, session);
                    } catch (Exception e) {
                        log.warn("cannot send message to user: " + user);
                    }
                }
                session.removeAttribute(NOTIFY_ALL.name());

            } else {
                sendService.sendWithKeyboard(chatId, "Введите сообщение для отправки всем пользователям:", session);
                session.setAttribute(SessionAttribute.NOTIFY_ALL.name(), true);
            }
        }

    }

    @Override
    public void handleCallback(Update update, Session session) {
    }
}





