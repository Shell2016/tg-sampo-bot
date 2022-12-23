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

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;


@Slf4j
public class RegisterHandler implements UpdateHandler {

    private final UserService userService;


    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
        User user = update.getMessage().getFrom();
        createAndAuthenticateUser(session, user);
    }

    @Override
    public void handleCallback(Update update, Session session) {
        User user = update.getCallbackQuery().getFrom();
        createAndAuthenticateUser(session, user);
    }

    private void createAndAuthenticateUser(Session session, User user) {
        UserReadDto userDto = userService.findById(user.getId()).orElse(null);
        if (userDto == null) {
            userDto = createUser(user);
            log.info("New user " + userDto + " have been successfully created");
        }
        authenticate(session, userDto);
    }


    private UserReadDto createUser(User user) {
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
        log.info("New user " + userReadDto + " have been successfully created");
        return userReadDto;
    }


    private static void authenticate(Session session, UserReadDto userDto) {
        if (userDto.getRole() != null) {
            session.setAttribute(HAS_ROLE.name(), userDto.getRole().name());
        }
        if (Status.ADMIN.equals(userDto.getStatus())) {
            session.setAttribute(STATUS.name(), Status.ADMIN.name());
        } else {
            session.setAttribute(STATUS.name(), Status.USER.name());
        }
        session.setAttribute(AUTHENTICATED.name(), true);
    }


}
