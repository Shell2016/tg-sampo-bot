package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.Collections;
import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.session.State.SET_ROLE_WAITING_FOR_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSetHandler implements UpdateHandler, CallbackHandler {

    private Role role;
    private final UserService userService;
    private final EventListHandler eventListHandler;
    private final UserSessionService sessionService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        User user = request.update().getMessage().getFrom();
        Long chatId = request.update().getMessage().getChatId();
        String fullName = request.update().getMessage().getText();
        String[] nameArr = fullName.split(" ");
        if (fullName.equals("Список коллективок")) {
            session.setDefaultState();
            sessionService.updateSession(session);
            return Collections.emptyList();
        }
        if (nameArr.length != 2) {
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Неверный формат: нужно 2 слова, разделённые пробелом")
                    .build());
        } else {
            String firstName = BotUtils.removeUnsupportedChars(nameArr[0]);
            String lastName = BotUtils.removeUnsupportedChars(nameArr[1]);

            if (userService.setUserRole(role, firstName, lastName, user.getId()).isPresent()) {
                session.setUserRole(role);
                session.setDefaultState();
                sessionService.updateSession(session);
                eventListHandler.handleUpdate(request);
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Успешная регистрация в системе! Теперь можно записываться на коллективки\uD83D\uDC83\uD83D\uDD7A")
                        .build());
            } else {
                session.setDefaultState();
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Пользователь не найден")
                        .build());
            }
        }
    }

    @Override
    public List<Response> handleCallback(Request request) {
        String data = request.update().getCallbackQuery().getData();
        Long chatId = request.update().getCallbackQuery().getMessage().getChatId();
        Integer messageId = request.update().getCallbackQuery().getMessage().getMessageId();
        role = null;
        if ("buttonLeader".equals(data)) {
            role = Role.LEADER;
        } else if ("buttonFollower".equals(data)) {
            role = Role.FOLLOWER;
        }
        UserSession session = request.session();
        session.setState(SET_ROLE_WAITING_FOR_NAME);
        sessionService.updateSession(session);
        return List.of(Response.builder()
                .type(ResponseType.EDIT_TEXT_MESSAGE)
                .chatId(chatId)
                .messageId(messageId)
                .message("Введите имя и фамилию:")
                .build());
    }
}
