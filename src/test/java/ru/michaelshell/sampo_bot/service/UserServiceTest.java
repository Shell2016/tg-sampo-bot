package ru.michaelshell.sampo_bot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.mapper.UserCreateEditDtoMapper;
import ru.michaelshell.sampo_bot.mapper.UserReadDtoMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserEventRepository userEventRepository;
    @Mock
    private UserCreateEditDtoMapper userCreateEditDtoMapper;
    @Mock
    private UserReadDtoMapper userReadDtoMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        UserCreateEditDto userCreateEditDto = getUserCreateEditDto();
        User user = getUser();
        UserReadDto userReadDto = getUserReadDto();
        doReturn(user).when(userCreateEditDtoMapper).map(userCreateEditDto);
        doReturn(user).when(userRepository).save(user);
        doReturn(userReadDto).when(userReadDtoMapper).map(user);

        UserReadDto actualResult = userService.createUser(userCreateEditDto);

        assertThat(actualResult).isEqualTo(userReadDto);
        verify(userRepository).save(user);
    }



    @Test
    void findById() {
    }

    @Test
    void promoteByUserName() {
    }

    @Test
    void setUserRole() {
    }

    @Test
    void registerOnEvent() {
    }

    @Test
    void testRegisterOnEvent() {
    }

    @Test
    void isAlreadyRegistered() {
    }

    private static User getUser() {
        return User.builder()
                .id(1L)
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private static UserReadDto getUserReadDto() {
        return UserReadDto.builder()
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private static UserCreateEditDto getUserCreateEditDto() {
        return UserCreateEditDto.builder()
                .id(1L)
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }
}