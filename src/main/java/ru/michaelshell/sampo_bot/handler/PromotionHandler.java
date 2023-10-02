package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.PROMOTION_WAITING_FOR_USERNAME;

@Component
@RequiredArgsConstructor
public class PromotionHandler implements UpdateHandler {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final BotProperties botProperties;

    @Override
    public void handleUpdate(Request request) {
        Session session = request.session();
        Message message = request.update().getMessage();
        User user = message.getFrom();
        Long chatId = message.getChatId();

        if (!checkPromotionRights(botProperties.admin().username(), user.getUserName())) {
            responseSender.sendWithKeyboardBottom(chatId, "Нет прав для выполнения команды", session);
            return;
        }
        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            String userName = request.update().getMessage().getText().trim();
            try {
                userService.promoteByUserName(userName);
                String success = """
                        Пользователю выданы права админа.
                        Если фунционал не заработал, ему нужно ввести команду
                        /clear чтобы очистить текущую сессию""";
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                responseSender.sendWithKeyboardBottom(chatId, success, session);
                return;
            } catch (NoSuchElementException e) {
                responseSender.sendWithKeyboardBottom(chatId, "Пользователь не найден", session);
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                return;
            }
        }
        responseSender.sendWithKeyboardBottom(chatId, "Введите имя для выдачи админских прав", session);
        session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), true);
    }

    boolean checkPromotionRights(String adminUsername, String nameToCheck) {
        return Objects.equals(adminUsername, nameToCheck);
    }
}
