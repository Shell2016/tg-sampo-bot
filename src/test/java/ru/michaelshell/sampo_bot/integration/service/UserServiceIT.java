package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

    private static final String PROMOTE_USERNAME = "test9";
    private static final Long PROMOTE_ID = 9L;


    private final UserService userService;

    @Test
    void findById() {
        Optional<UserReadDto> readDto = userService.findById(1L);

        readDto.ifPresent(user -> assertThat(user.getUserName()).isEqualTo("test1"));
    }

    @Test
    void promoteByUserName() {
        userService.promoteByUserName(PROMOTE_USERNAME);
        Optional<UserReadDto> readDto = userService.findById(PROMOTE_ID);

        readDto.ifPresent(user -> assertThat(user.getStatus()).isEqualTo(Status.ADMIN));
    }


    @Test
    void createUser() {
    }

    @Test
    void setUserRole() {
    }

    @Test
    void registerOnEvent() {
    }

    @Test
    void coupleRegisterOnEvent() {
    }

    @Test
    void isAlreadyRegistered() {
    }
}
