package ru.michaelshell.sampo_bot.integration.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.integration.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
public class EventRepositoryIT extends IntegrationTestBase {

    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "RS-Main";
    private static final LocalDateTime EVENT_TIME = LocalDateTime.of(2022, 12, 22, 3, 43);

    private final EventRepository eventRepository;

    @Test
    void findById() {
        Optional<Event> eventOptional = eventRepository.findById(EVENT_ID);
        assertThat(eventOptional).isPresent();
        eventOptional.ifPresent(event -> assertThat(event.getName()).isEqualTo(EVENT_NAME));
    }

    @Test
    void findByNameAndTime() {
        Optional<Event> optionalEvent = eventRepository.findByNameAndTime(EVENT_NAME, EVENT_TIME);

        assertThat(optionalEvent).isPresent();
        assertThat(optionalEvent.get().getId()).isEqualTo(EVENT_ID);
    }

    @Test
    void deleteByNameAndTime() {
        assertThat(eventRepository.deleteByNameAndTime(EVENT_NAME, EVENT_TIME)).isEqualTo(1);
        assertThat(eventRepository.findById(EVENT_ID)).isEmpty();
    }
}
