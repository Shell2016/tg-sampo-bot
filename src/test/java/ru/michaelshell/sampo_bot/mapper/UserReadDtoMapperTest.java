package ru.michaelshell.sampo_bot.mapper;

import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.UserReadDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserReadDtoMapperTest {

    private final UserReadDtoMapper mapper = new UserReadDtoMapper();

    @Test
    void map() {
        User user = getUser();

        UserReadDto actualResult = mapper.map(user);
        UserReadDto expectedResult = getUserReadDto();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static UserReadDto getUserReadDto() {
        return UserReadDto.builder()
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50, 0))
                .build();
    }

    private static User getUser() {
        return User.builder()
                .id(1L)
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50, 0))
                .build();
    }
}