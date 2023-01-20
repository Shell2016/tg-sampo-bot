package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
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
@Component
@RequiredArgsConstructor
public class PromotionHandler implements UpdateHandler {

    private final SendServiceImpl sendService;
    private final UserService userService;
    private final BotProperties botProperties;

    @Override
    public void handleUpdate(Update update, Session session) {
        Message message = update.getMessage();
        User user = message.getFrom();
        Long chatId = message.getChatId();

        if (!checkPromotionRights(botProperties.admin().id(), user.getId(),
                botProperties.admin().username(), user.getUserName())) {
            sendService.sendWithKeyboard(chatId, "Нет прав для выполнения команды", session);
            return;
        }
        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            String userName = update.getMessage().getText().trim();
            try {
                userService.promoteByUserName(userName);
                String success = """
                        Пользователю выданы права админа.
                        Если фунционал не заработал, ему нужно ввести команду
                        /clear чтобы очистить текущую сессию""";
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                sendService.sendWithKeyboard(chatId, success, session);
                return;
            } catch (NoSuchElementException e) {
                sendService.sendWithKeyboard(chatId, "Пользователь не найден", session);
                session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), false);
                return;
            }
        }
        sendService.sendWithKeyboard(chatId, "Введите имя для выдачи админских прав", session);
        session.setAttribute(PROMOTION_WAITING_FOR_USERNAME.name(), true);
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


    boolean checkPromotionRights(Long adminId, Long idToCheck, String adminUsername, String nameToCheck) {
        return Objects.equals(idToCheck, adminId) || Objects.equals(adminUsername, nameToCheck);
    }


}
