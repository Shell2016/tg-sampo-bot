package ru.michaelshell.sampo_bot.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.sampo_bot.database.entity.*;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.mapper.UserCreateEditDtoMapper;
import ru.michaelshell.sampo_bot.mapper.UserReadDtoMapper;
import ru.michaelshell.sampo_bot.service.UserService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testUserName";
    private static final String TEST_EVENT_NAME = "RS-Main";
    private static final LocalDateTime TEST_EVENT_TIME = LocalDateTime.of(2023, 1, 31, 19, 0);

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
        User user = getUser();
        UserReadDto userReadDto = getUserReadDto();
        doReturn(Optional.of(user)).when(userRepository).findById(TEST_USER_ID);
        doReturn(userReadDto).when(userReadDtoMapper).map(user);

        Optional<UserReadDto> result = userService.findById(TEST_USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo(TEST_USERNAME);
    }

    @Test
    void promoteByUserName() {
        User user = getUser();
        doReturn(Optional.of(user)).when(userRepository).findByUserName(TEST_USERNAME);

        userService.promoteByUserName(TEST_USERNAME);

        assertThat(user.getStatus()).isEqualTo(Status.ADMIN);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void promoteByUserNameShouldThrowExceptionIfUsernameNotFound() {
        doReturn(Optional.empty()).when(userRepository).findByUserName("dummyName");

        assertThrows(NoSuchElementException.class, () -> userService.promoteByUserName("dummyName"));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void setUserRole() {
        User user = getUser();
        UserReadDto userReadDto = UserReadDto.builder()
                .userName(TEST_USERNAME)
                .firstName("Michael")
                .lastName("Shell")
                .role(Role.LEADER)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
        doReturn(Optional.of(user)).when(userRepository).findById(TEST_USER_ID);
        doReturn(userReadDto).when(userReadDtoMapper).map(user);

        Optional<UserReadDto> result = userService.setUserRole(Role.LEADER, "Michael", "Shell", TEST_USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userReadDto);
        assertThat(user.getRole()).isSameAs(Role.LEADER);
        assertThat(user.getFirstName()).isEqualTo("Michael");
        assertThat(user.getLastName()).isEqualTo("Shell");
        verifyNoMoreInteractions(userRepository);
        verify(userReadDtoMapper).map(user);
    }

    @Test
    void setUserRoleIfUserDoesNotExist() {
        doReturn(Optional.empty()).when(userRepository).findById(1000L);

        Optional<UserReadDto> result = userService.setUserRole(Role.LEADER, "Michael", "Shell", 1000L);

        assertThat(result).isEmpty();
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userReadDtoMapper);
    }

    @Test
    void registerOnEventSolo() {
        User user = getUser();
        Event event = getEvent();
        EventGetDto eventDto = getEventDto();
        doReturn(Optional.of(user)).when(userRepository).findById(TEST_USER_ID);
        doReturn(Optional.of(event)).when(eventRepository).findByNameAndTime(TEST_EVENT_NAME, TEST_EVENT_TIME);

        UserEvent userEvent = userService.registerOnEvent(eventDto, TEST_USER_ID);

        assertThat(userEvent.getUser()).isEqualTo(user);
        assertThat(userEvent.getEvent()).isEqualTo(event);
        verify(userEventRepository).save(userEvent);
    }

    @Test
    void registerOnEventWithPartner() {
        User user = getUser();
        Event event = getEvent();
        doReturn(Optional.of(user)).when(userRepository).findById(TEST_USER_ID);
        doReturn(Optional.of(event)).when(eventRepository).findById(1L);

        UserEvent userEvent = userService.registerOnEvent(1L, TEST_USER_ID, "Lena", "Ivanova");

        assertThat(userEvent.getUser()).isEqualTo(user);
        assertThat(userEvent.getEvent()).isEqualTo(event);
        assertThat(userEvent.getPartnerFullname()).isEqualTo("Ivanova Lena");
        verify(userEventRepository).save(userEvent);
    }

    @Test
    void isAlreadyRegistered() {
        EventGetDto eventDto = getEventDto();
        User user = getUser();
        Event event = getEvent();
        UserEvent userEvent = UserEvent.builder()
                .event(event)
                .user(user)
                .build();
        doReturn(Optional.of(userEvent))
                .when(userEventRepository).findByUserIdAndEventNameAndEventTime(TEST_USER_ID, TEST_EVENT_NAME, TEST_EVENT_TIME);

        assertTrue(userService.isAlreadyRegistered(eventDto, TEST_USER_ID));
    }


    private static User getUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .userName(TEST_USERNAME)
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private static UserReadDto getUserReadDto() {
        return UserReadDto.builder()
                .userName(TEST_USERNAME)
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private static UserCreateEditDto getUserCreateEditDto() {
        return UserCreateEditDto.builder()
                .id(TEST_USER_ID)
                .userName(TEST_USERNAME)
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(null)
                .status(Status.USER)
                .registeredAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private Event getEvent() {
        return Event.builder()
                .id(1L)
                .name(TEST_EVENT_NAME)
                .time(TEST_EVENT_TIME)
                .createdBy("shell")
                .build();
    }

    private EventGetDto getEventDto() {
        return EventGetDto.builder()
                .name(TEST_EVENT_NAME)
                .time(TEST_EVENT_TIME)
                .build();
    }
}