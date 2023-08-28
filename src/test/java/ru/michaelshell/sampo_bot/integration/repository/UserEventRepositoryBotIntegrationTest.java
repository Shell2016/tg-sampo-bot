package ru.michaelshell.sampo_bot.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.integration.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class UserEventRepositoryBotIntegrationTest extends IntegrationTestBase {

    private static final String EVENT_NAME = "RS-Main";
    private static final LocalDateTime EVENT_TIME = LocalDateTime.of(2022, 12, 22, 3, 43);
    public static final Integer USER_EVENT_COUNT = 16;

    private final UserEventRepository userEventRepository;

    @Test
    void findAllByNameAndTime() {
        List<UserEvent> userEvents = userEventRepository.findAllByNameAndTime(EVENT_NAME, EVENT_TIME);

        assertThat(userEvents).hasSize(USER_EVENT_COUNT);
    }

    @Test
    void findByUserIdAndEventNameAndEventTime() {

        Optional<UserEvent> result = userEventRepository.findByUserIdAndEventNameAndEventTime(1L, EVENT_NAME, EVENT_TIME);
        assertThat(result).isPresent();
    }

    @Test
    void findByUserIdAndEventNameAndEventTimeIfNotPresent() {

        Optional<UserEvent> result = userEventRepository.findByUserIdAndEventNameAndEventTime(100L, EVENT_NAME, EVENT_TIME);
        assertThat(result).isEmpty();
    }
}