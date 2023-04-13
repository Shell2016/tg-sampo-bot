package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.michaelshell.sampo_bot.IntegrationTestBase;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
public class EventServiceIT extends IntegrationTestBase {

    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "RS-Main";
    private static final String EVENT_TEST_NAME = "testTitle";

    private final EventService eventService;
    private final EventRepository eventRepository;

    @Test
    void updateEventTitle() {
        Event event = eventRepository.findById(EVENT_ID).get();

        assertThat(event.getName()).isEqualTo(EVENT_NAME);

        eventService.updateEventTitle(EVENT_ID, EVENT_TEST_NAME);
        eventRepository.flush();
        Event updatedEvent = eventRepository.findById(EVENT_ID).get();
        String result = updatedEvent.getName();

        assertThat(EVENT_TEST_NAME).isEqualTo(result);
    }

    @Test
    void updateEventTitleShouldThrowExceptionIfWrongId() {
        Optional<EventReadDto> result = eventService.updateEventTitle(1000L, EVENT_TEST_NAME);

        assertThat(result).isEmpty();
    }

}
