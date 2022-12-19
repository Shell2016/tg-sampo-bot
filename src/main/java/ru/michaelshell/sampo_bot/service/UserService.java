package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    void save(User user) {
        User.builder()
                .id()
                        .build();
        userRepository.save(user);
    }
}
