package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD_ASYNC;
import static ru.michaelshell.sampo_bot.session.State.NOTIFY_ALL;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyAllHandler implements UpdateHandler {

    private final UserService userService;
    private final UserSessionService sessionService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        Long chatId = message.getChatId();
        if (AuthUtils.isAdmin(session)) {
            String msgTxt = message.getText();
            if (session.getState() == NOTIFY_ALL) {
                log.info("Sending message to all users:\n" + msgTxt);
                List<Response> responseList = userService.findAll().stream()
                        .map(user -> Response.builder()
                                .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD_ASYNC)
                                .keyboard(AuthUtils.getBottomKeyboard(session))
                                .chatId(user.getId())
                                .message(msgTxt)
                                .build())
                        .toList();
                session.setDefaultState();
                sessionService.updateSession(session);
                return responseList;
            } else {
                session.setState(NOTIFY_ALL);
                sessionService.updateSession(session);
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Введите сообщение для отправки всем пользователям:")
                        .build());
            }

        } else {
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Нет прав для данной операции!")
                    .build());
        }
    }
}
