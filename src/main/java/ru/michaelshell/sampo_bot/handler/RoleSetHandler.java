package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.SET_ROLE_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.util.BotUtils.TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX;


@Slf4j
public class RoleSetHandler implements UpdateHandler {

    private Role role;

    private final UserService userService;
    private final SendServiceImpl sendService;


    public RoleSetHandler(SendServiceImpl sendService, UserService userService) {
        this.userService = userService;
        this.sendService = sendService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        User user = update.getMessage().getFrom();
        Long chatId = update.getMessage().getChatId();
        String fullName = update.getMessage().getText();
        String[] nameArr = fullName.split(" ");
        if (nameArr.length != 2) {
            sendService.sendWithKeyboard(chatId, "Неверный формат, введите еще раз", session);
        } else {
            String firstName = nameArr[0].replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ").trim();
            String lastName = nameArr[1].replaceAll(TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX, " ").trim();

            if (userService.setUserRole(role, firstName, lastName, user.getId()).isPresent()) {
                session.setAttribute(HAS_ROLE.name(), role.name());
                session.removeAttribute(SET_ROLE_WAITING_FOR_NAME.name());
                sendService.sendWithKeyboard(chatId, "Теперь можно записываться на коллективки", session);
            } else {
                session.removeAttribute(SET_ROLE_WAITING_FOR_NAME.name());
                sendService.sendWithKeyboard(chatId, "Что-то пошло не так", session);
            }

        }


    }

    @Override
    public void handleCallback(Update update, Session session) {
        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        role = null;
        if ("buttonLeader".equals(data)) {
            role = Role.LEADER;
        } else if ("buttonFollower".equals(data)) {
            role = Role.FOLLOWER;
        }
        sendService.sendWithKeyboard(chatId, "Введите имя и фамилию (именно в таком порядке)", session);
        session.setAttribute(SET_ROLE_WAITING_FOR_NAME.name(), true);
    }


}
