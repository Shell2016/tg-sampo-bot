package ru.michaelshell.sampo_bot.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        User updatedUser = userRepository.findById(18L).get();

        assertThat(updatedUser.getUserName()).isEqualTo("test18");
    }

    @Test
    void findByUserName() {
        Optional<User> optionalUser = userRepository.findByUserName("test17");

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getId()).isEqualTo(17L);
    }
}
