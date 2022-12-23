package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;
import ru.michaelshell.sampo_bot.mapper.UserCreateEditDtoMapper;
import ru.michaelshell.sampo_bot.mapper.UserReadDtoMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
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
}
