package ru.michaelshell.sampo_bot.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RequiredArgsConstructor
public class UserRepositoryIT extends IntegrationTestBase {


    private final UserRepository userRepository;

    @Test
    void checkUpdateUserNameForExistingName() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.updateUserName(18L, "test17"));
    }

    @Test
    void updateUserName() {
        int resultCount = userRepository.updateUserName(18L, "test18");

        assertEquals(1, resultCount);
    }
}
