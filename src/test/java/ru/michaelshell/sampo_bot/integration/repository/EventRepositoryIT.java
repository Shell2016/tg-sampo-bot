package ru.michaelshell.sampo_bot.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
public class EventRepositoryIT extends IntegrationTestBase {

    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "RS-Main";

    private final EventRepository eventRepository;

    @Test
    void findById() {
        Optional<Event> eventOptional = eventRepository.findById(EVENT_ID);
        assertThat(eventOptional).isPresent();
        eventOptional.ifPresent(event -> assertThat(event.getName()).isEqualTo(EVENT_NAME));
    }

}
