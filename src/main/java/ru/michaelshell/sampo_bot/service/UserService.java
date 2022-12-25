package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserEventRepository userEventRepository;
    private final UserCreateEditDtoMapper userCreateEditDtoMapper;
    private final UserReadDtoMapper userReadDtoMapper;

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

    public Optional<UserReadDto> findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .map(userReadDtoMapper::map);
    }

    @Transactional
    public void promoteByUserName(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setStatus(Status.ADMIN);
        userRepository.saveAndFlush(user);
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
                .map(userRepository::saveAndFlush)
                .map(userReadDtoMapper::map);
    }

    @Transactional
    public void registerOnEvent(EventGetDto eventDto, Long userId) {
        Event event = eventRepository.findEventByNameAndTime(eventDto.getName(), eventDto.getTime()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        UserEvent userEvent = UserEvent.builder()
                .signedAt(LocalDateTime.now())
                .build();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEventRepository.save(userEvent);
    }

    @Transactional
    public void registerOnEvent(Long eventId, Long userId, String partnerFirstName, String partnerLastName) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        UserEvent userEvent = UserEvent.builder()
                .partnerFullname(partnerLastName + " " + partnerFirstName)
                .signedAt(LocalDateTime.now())
                .build();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEventRepository.save(userEvent);
    }

    public boolean isAlreadyRegistered(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        return user.getUserEvents().stream()
                .anyMatch(userEvent -> userEvent.getEvent().equals(event));
    }

    public boolean isAlreadyRegistered(EventGetDto eventGetDto, Long userId) {
        Event event = eventRepository.findEventByNameAndTime(eventGetDto.getName(), eventGetDto.getTime()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        return user.getUserEvents().stream()
                .anyMatch(userEvent -> userEvent.getEvent().equals(event));
    }


}
