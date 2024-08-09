package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static ru.michaelshell.sampo_bot.session.State.PROMOTION_WAITING_FOR_USERNAME;
import static ru.michaelshell.sampo_bot.util.AuthUtils.getBottomKeyboard;

@Component
@RequiredArgsConstructor
public class PromotionHandler implements UpdateHandler {

    private final UserService userService;
    private final UserSessionService sessionService;
    @Value("${bot.admin}")
    private String botAdmin;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        User user = message.getFrom();
        Long chatId = message.getChatId();

        if (!checkPromotionRights(botAdmin, user.getUserName())) {
            return List.of(Response.builder()
                    .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .chatId(chatId)
                    .message("Нет прав для выполнения команды")
                    .keyboard(getBottomKeyboard(session))
                    .build());
        }
        if (session.getState() == PROMOTION_WAITING_FOR_USERNAME) {
            String userName = request.update().getMessage().getText().trim();
            try {
                userService.promoteByUserName(userName);
                String success = """
                        Пользователю выданы права админа.
                        Если фунционал не заработал, ему нужно ввести команду
                        /clear чтобы очистить текущую сессию""";
                session.setDefaultState();
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .chatId(chatId)
                        .message(success)
                        .keyboard(getBottomKeyboard(session))
                        .build());

            } catch (NoSuchElementException e) {
                session.setDefaultState();
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .chatId(chatId)
                        .message("Пользователь не найден")
                        .keyboard(getBottomKeyboard(session))
                        .build());
            }
        }
        session.setState(PROMOTION_WAITING_FOR_USERNAME);
        sessionService.updateSession(session);
        return List.of(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .chatId(chatId)
                .message("Введите имя для выдачи админских прав")
                .keyboard(getBottomKeyboard(session))
                .build());
    }

    boolean checkPromotionRights(String adminUsername, String nameToCheck) {
        return Objects.equals(adminUsername, nameToCheck);
    }
}
