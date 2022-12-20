package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.service.UserService;

import java.time.LocalDateTime;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.AUTHENTICATED;


@Slf4j
public class RegisterHandler implements UpdateHandler {

    private final UserService userService;


    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        User user = update.getMessage().getFrom();
        if (userService.findById(user.getId()).isEmpty()) {
            createUser(user);
        }
        session.setAttribute(AUTHENTICATED, true);
    }

    private void createUser(User user) {
        UserCreateEditDto dto = UserCreateEditDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.now())
                .build();

        UserReadDto userReadDto = userService.createUser(dto);
        log.info("New user " + userReadDto.getUserName() + " have been successfully created");
    }


}
