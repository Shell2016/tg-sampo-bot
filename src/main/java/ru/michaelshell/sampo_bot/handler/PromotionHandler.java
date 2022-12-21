package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionManager;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.config.BotProperties;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.NoSuchElementException;
import java.util.Objects;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.PROMOTION_WAITING_FOR_USERNAME;


@Slf4j
public class PromotionHandler implements UpdateHandler {

    private final SendServiceImpl sendService;
    private final UserService userService;
    private final BotProperties botProperties;


    public PromotionHandler(SendServiceImpl sendService, UserService userService, BotProperties botProperties) {
        this.userService = userService;
        this.sendService = sendService;
        this.botProperties = botProperties;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        Message message = update.getMessage();
        User user = message.getFrom();
        Long chatId = message.getChatId();


        if (!checkPromotionRights(Long.valueOf(botProperties.adminId()), user.getId(),
                botProperties.adminUsername(), user.getUserName())) {
            sendService.send(chatId, "Нет прав для выполнения команды");
            return;
        }
        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            // TODO: 21.12.2022 Promotion
            String userName = update.getMessage().getText();
            try {
                userService.promoteByUserName(userName);
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                sendService.send(chatId, ("Пользователю %s выданы права админа.\n" +
                        "Если функционал не заработал, ему нужно будет ввести команду /clear," +
                        " чтобы очистить текущую сессию").formatted(userName));
                return;
            } catch (NoSuchElementException e) {
                sendService.send(chatId, "Пользователь не найден");
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                return;
            }

        }

        sendService.send(user.getId(), "Enter username for promotion");
        session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), true);


    }


    boolean checkPromotionRights(Long adminId, Long idToCheck, String adminUsername, String nameToCheck) {
        return Objects.equals(idToCheck, adminId) || Objects.equals(adminUsername, nameToCheck);
    }


}
