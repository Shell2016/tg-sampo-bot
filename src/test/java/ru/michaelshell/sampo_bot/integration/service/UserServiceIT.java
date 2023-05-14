package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.michaelshell.sampo_bot.database.entity.*;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.integration.IntegrationTestBase;
import ru.michaelshell.sampo_bot.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@RequiredArgsConstructor
class UserServiceIT extends IntegrationTestBase {

    private static final String PROMOTE_USERNAME = "test9";
    private static final Long PROMOTE_ID = 9L;


    private final UserService userService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

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
        Optional<UserReadDto> user = userService.setUserRole(Role.FOLLOWER,
                "newFollowerName",
                "newFollowerLastName",
                1L);

        assertThat(user).isPresent();

        assertAll(() -> assertThat(user.get().getFirstName()).isEqualTo("newFollowerName"),
                () -> assertThat(user.get().getLastName()).isEqualTo("newFollowerLastName"),
                () -> assertThat(user.get().getRole()).isSameAs(Role.FOLLOWER));
    }

    @Test
    void setUserRoleIfWrongUserId() {
        Optional<UserReadDto> user = userService.setUserRole(Role.FOLLOWER,
                "newFollowerName",
                "newFollowerLastName",
                100L);

        assertThat(user).isEmpty();
    }

    @Test
    void registerOnEvent() {
        LocalDateTime time = LocalDateTime.parse("2022-01-22T03:43:00.000000");
        User user = userRepository.findById(1L).get();
        Event event = eventRepository.findByNameAndTime("Beginner", time).get();
        UserEvent userEvent = userService.registerOnEvent(EventGetDto.builder()
                .name("Beginner")
                .time(time)
                .build(), 1L);

        assertThat(userEvent.getUser()).isEqualTo(user);
        assertThat(userEvent.getEvent()).isEqualTo(event);
    }

    @Test
    void registerOnEventIfAlreadyRegistered() {
        LocalDateTime time = LocalDateTime.parse("2022-12-22T03:43:00.000000");

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.registerOnEvent(EventGetDto.builder()
                        .name("RS-Main")
                        .time(time)
                        .build(), 1L));
    }

    @Test
    void registerOnEventIfEventDoesNotExist() {
        LocalDateTime time = LocalDateTime.parse("2022-12-22T03:43:10.000000");

        assertThrows(NoSuchElementException.class,
                () -> userService.registerOnEvent(EventGetDto.builder()
                        .name("RS-Main")
                        .time(time)
                        .build(), 1L));
    }

    @Test
    void coupleRegisterOnEvent() {
        UserEvent userEvent = userService.registerOnEvent(2L, 1L, "partnerFirstName", "partnerLastName");

        assertThat(userEvent).isNotNull();
    }

    @Test
    void coupleRegisterOnEventIfAlreadyRegistered() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.registerOnEvent(1L, 1L, "partnerFirstName", "partnerLastName"));
    }

    @Test
    void coupleRegisterOnEventIfEventDoesNotExist() {
        assertThrows(NoSuchElementException.class,
                () -> userService.registerOnEvent(100L, 1L, "partnerFirstName", "partnerLastName"));
    }

    @Test
    void isAlreadyRegistered() {
        LocalDateTime time = LocalDateTime.parse("2022-12-22T03:43:00.000000");
        EventGetDto eventRegistered = EventGetDto.builder()
                .name("RS-Main")
                .time(time)
                .build();

        boolean result = userService.isAlreadyRegistered(eventRegistered, 1L);
        assertThat(result).isTrue();
    }

    @Test
    void isAlreadyRegisteredFalse() {
        LocalDateTime time = LocalDateTime.parse("2022-01-22T03:43:00.000000");
        EventGetDto eventNotRegistered = EventGetDto.builder()
                .name("Beginner")
                .time(time)
                .build();

        boolean result = userService.isAlreadyRegistered(eventNotRegistered, 1L);
        assertThat(result).isFalse();
    }

    @Test
    void updateUserName() {
        int result = userService.updateUserName(1L, "updatedUserName");

        Assertions.assertEquals(1, result);
    }

    @Test
    void updateUserNameIfUsernameAlreadyExists() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.updateUserName(1L, "test2"));
    }

    @Test
    void updateUserNameIfUserDoesNotExist() {
        int result = userService.updateUserName(100L, "updatedUserName");

        Assertions.assertEquals(0, result);
    }
}
