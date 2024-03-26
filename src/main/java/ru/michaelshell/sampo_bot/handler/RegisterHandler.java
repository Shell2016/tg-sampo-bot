package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RegisterHandler implements UpdateHandler, CallbackHandler, InlineQueryHandler {

    private final UserService userService;
    private final UserSessionService sessionService;

    @Override
    public void handleUpdate(Request request) {
        User user = request.update().getMessage().getFrom();
        createAndAuthenticateUser(request.session(), user);
    }

    @Override
    public void handleCallback(Request request) {
        User user = request.update().getCallbackQuery().getFrom();
        createAndAuthenticateUser(request.session(), user);
    }

    @Override
    public void handleInlineQuery(Request request) {
        User user = request.update().getInlineQuery().getFrom();
        createAndAuthenticateUser(request.session(), user);
    }

    private void createAndAuthenticateUser(UserSession session, User user) {
        UserReadDto userDto = userService.findById(user.getId()).orElse(null);
        if (userDto == null) {
            userDto = createUser(user);
        } else if (userDto.getUserName() == null && user.getUserName() != null) {
            userService.updateUserName(user.getId(), user.getUserName());
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
        return userService.createUser(dto);
    }

    private void authenticate(UserSession session, UserReadDto userDto) {
        if (userDto.getRole() != null) {
            session.setUserRole(userDto.getRole());
        }
        session.setUserStatus(userDto.getStatus());
        session.setAuthenticated(true);
        sessionService.updateSession(session);
    }
}
