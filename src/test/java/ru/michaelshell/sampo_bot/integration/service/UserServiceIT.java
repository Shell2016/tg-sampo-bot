package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


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
        UserCreateEditDto userDto = UserCreateEditDto.builder()
                .id(100L)
                .userName("test_user")
                .firstName("test_firstname")
                .registeredAt(LocalDateTime.now())
                .build();

        UserReadDto dto = userService.createUser(userDto);
        Optional<UserReadDto> readDto = userService.findById(100L);

        assertThat(readDto).isPresent();
        readDto.ifPresent(user -> {
            assertEquals(dto.getUserName(), user.getUserName());
            assertEquals(dto.getFirstName(), user.getFirstName());
            assertEquals(dto.getRegisteredAt(), user.getRegisteredAt());
            assertEquals(dto.getLastName(), user.getLastName());
            assertSame(dto.getRole(), user.getRole());
        });

    }

    @Test
    void findAllUserIds() {
        List<User> resultList = userService.findAll();

        assertThat(resultList).hasSize(18);
        assertThat(resultList.get(0).getId()).isEqualTo(1L);
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
