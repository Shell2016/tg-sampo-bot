package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.*;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.mapper.UserCreateEditDtoMapper;
import ru.michaelshell.sampo_bot.mapper.UserReadDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserEventRepository userEventRepository;
    private final UserCreateEditDtoMapper userCreateEditDtoMapper;
    private final UserReadDtoMapper userReadDtoMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public UserReadDto createUser(UserCreateEditDto dto) {
        return Optional.of(dto)
                .map(userCreateEditDtoMapper::map)
                .map(userRepository::save)
                .map(userReadDtoMapper::map)
                .orElseThrow();
    }

    public Optional<UserReadDto> findById(Long id) {
        return userRepository.findById(id)
                .map(userReadDtoMapper::map);
    }

    @Transactional
    public void promoteByUserName(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setStatus(Status.ADMIN);
    }

    @Transactional
    public Optional<UserReadDto> setUserRole(Role role, String firstName, String lastName, Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(role);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    return user;
                })
                .map(userReadDtoMapper::map);
    }

    @Transactional
    public UserEvent registerOnEvent(EventGetDto eventDto, Long userId) {
        Event event = eventRepository.findByNameAndTime(eventDto.getName(), eventDto.getTime()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        UserEvent userEvent = UserEvent.builder()
                .signedAt(LocalDateTime.now())
                .build();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEventRepository.save(userEvent);
        return userEvent;
    }

    @Transactional
    public UserEvent registerOnEvent(Long eventId, Long userId, String partnerFirstName, String partnerLastName) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        UserEvent userEvent = UserEvent.builder()
                .partnerFullname(partnerFirstName + " " + partnerLastName)
                .signedAt(LocalDateTime.now())
                .build();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEventRepository.save(userEvent);
        return userEvent;
    }

    public boolean isAlreadyRegistered(EventGetDto eventGetDto, Long userId) {
        return userEventRepository
                .findByUserIdAndEventNameAndEventTime(userId, eventGetDto.getName(), eventGetDto.getTime())
                .isPresent();
    }

    @Transactional
    public int updateUserName(Long id, String userName) {
        return userRepository.updateUserName(id, userName);
    }
}
