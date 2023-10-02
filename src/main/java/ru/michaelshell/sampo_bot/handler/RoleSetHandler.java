package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.service.UserService;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.SET_ROLE_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.util.BotUtils.TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX;


@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSetHandler implements UpdateHandler, CallbackHandler {

    private Role role;

    private final UserService userService;
    private final ResponseSender responseSender;
    private final EventListHandler eventListHandler;

    @Override
    public void handleUpdate(Request request) {
        Session session = request.session();
        User user = request.update().getMessage().getFrom();
        Long chatId = request.update().getMessage().getChatId();
        String fullName = request.update().getMessage().getText();
        String[] nameArr = fullName.split(" ");
        if (nameArr.length != 2) {
            responseSender.sendWithKeyboardBottom(chatId, "Неверный формат: нужно 2 слова, разделённые пробелом", session);
        } else {
            String firstName = nameArr[0].replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ").trim();
            String lastName = nameArr[1].replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ").trim();

            if (userService.setUserRole(role, firstName, lastName, user.getId()).isPresent()) {
                session.setAttribute(HAS_ROLE.name(), role.name());
                session.removeAttribute(SET_ROLE_WAITING_FOR_NAME.name());
                responseSender.sendWithKeyboardBottom(chatId, "Теперь можно записываться на коллективки\uD83D\uDC83\uD83D\uDD7A", session);
                eventListHandler.handleUpdate(request);
            } else {
                session.removeAttribute(SET_ROLE_WAITING_FOR_NAME.name());
                responseSender.sendWithKeyboardBottom(chatId, "Что-то пошло не так", session);
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
        responseSender.edit(chatId, messageId, "Введите имя и фамилию (желательно в этом порядке)");
        request.session().setAttribute(SET_ROLE_WAITING_FOR_NAME.name(), true);
    }
}
