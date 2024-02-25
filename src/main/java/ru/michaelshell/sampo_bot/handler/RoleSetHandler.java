package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import static ru.michaelshell.sampo_bot.session.State.SET_ROLE_WAITING_FOR_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSetHandler implements UpdateHandler, CallbackHandler {

    private Role role;
    private final UserService userService;
    private final ResponseSender responseSender;
    private final EventListHandler eventListHandler;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        User user = request.update().getMessage().getFrom();
        Long chatId = request.update().getMessage().getChatId();
        String fullName = request.update().getMessage().getText();
        String[] nameArr = fullName.split(" ");
        if (fullName.equals(BotUtils.EVENT_LIST_COMMAND)) {
            session.setDefaultState();
            sessionService.updateSession(session);
            return;
        }
        if (nameArr.length != 2) {
            responseSender.sendWithKeyboardBottom(chatId, "Неверный формат: нужно 2 слова, разделённые пробелом", session);
        } else {
            String firstName = BotUtils.removeUnsupportedChars(nameArr[0]);
            String lastName = BotUtils.removeUnsupportedChars(nameArr[1]);

            if (userService.setUserRole(role, firstName, lastName, user.getId()).isPresent()) {
                session.setUserRole(role);
                session.setDefaultState();
                sessionService.updateSession(session);
                responseSender.sendWithKeyboardBottom(chatId, "Успешная регистрация в системе! Теперь можно записываться на коллективки\uD83D\uDC83\uD83D\uDD7A", session);
                eventListHandler.handleUpdate(request);
            } else {
                session.setDefaultState();
                sessionService.updateSession(session);
                responseSender.sendWithKeyboardBottom(chatId, "Пользователь не найден", session);
            }
        }
    }

    @Override
    public void handleCallback(Request request) {
        String data = request.update().getCallbackQuery().getData();
        Long chatId = request.update().getCallbackQuery().getMessage().getChatId();
        Integer messageId = request.update().getCallbackQuery().getMessage().getMessageId();
        role = null;
        if ("buttonLeader".equals(data)) {
            role = Role.LEADER;
        } else if ("buttonFollower".equals(data)) {
            role = Role.FOLLOWER;
        }
        responseSender.edit(chatId, messageId, "Введите ваши имя и фамилию:");
        UserSession session = request.session();
        session.setState(SET_ROLE_WAITING_FOR_NAME);
        sessionService.updateSession(session);
    }
}
