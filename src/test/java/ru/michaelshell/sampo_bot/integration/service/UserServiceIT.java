package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.annotation.IT;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;

@IT
@RequiredArgsConstructor
public class UserServiceIT {

    private final UserRepository userRepository;

    @Test
    void test() {

    }
}
