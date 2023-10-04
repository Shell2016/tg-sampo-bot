package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ru.michaelshell.sampo_bot.session.State.PROMOTION_WAITING_FOR_USERNAME;

@Component
@RequiredArgsConstructor
public class PromotionHandler implements UpdateHandler {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final BotProperties botProperties;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        User user = message.getFrom();
        Long chatId = message.getChatId();

        if (!checkPromotionRights(botProperties.admin().username(), user.getUserName())) {
            responseSender.sendWithKeyboardBottom(chatId, "Нет прав для выполнения команды", session);
            return;
        }
        if (session.getState() == PROMOTION_WAITING_FOR_USERNAME) {
            String userName = request.update().getMessage().getText().trim();
            try {
                userService.promoteByUserName(userName);
                String success = """
                        Пользователю выданы права админа.
                        Если фунционал не заработал, ему нужно ввести команду
                        /clear чтобы очистить текущую сессию""";
                responseSender.sendWithKeyboardBottom(chatId, success, session);
                session.setDefaultState();
                sessionService.updateSession(session);
                return;
            } catch (NoSuchElementException e) {
                responseSender.sendWithKeyboardBottom(chatId, "Пользователь не найден", session);
                session.setDefaultState();
                sessionService.updateSession(session);
                return;
            }
        }
        responseSender.sendWithKeyboardBottom(chatId, "Введите имя для выдачи админских прав", session);
        session.setState(PROMOTION_WAITING_FOR_USERNAME);
        sessionService.updateSession(session);
    }

    boolean checkPromotionRights(String adminUsername, String nameToCheck) {
        return Objects.equals(adminUsername, nameToCheck);
    }
}
