package ru.michaelshell.sampo_bot.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.mapper.EventReadDtoMapper;
import ru.michaelshell.sampo_bot.service.EventService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "RS-Main";
    private static final String EVENT_TEST_NAME = "testTitle";
    private static final LocalDateTime TIME = LocalDateTime.of(2023, 1, 31, 20, 30);
    private static final String EVENT_TEST_INFO = "testInfo";

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventReadDtoMapper eventReadDtoMapper;
    @InjectMocks
    private EventService eventService;

    @Test
    void updateEventTitle() {
        Event event = getEvent();

        doReturn(Optional.of(event)).when(eventRepository).findById(EVENT_ID);
        doReturn(getReadDto()).when(eventReadDtoMapper).map(event);

        Optional<EventReadDto> result = eventService.updateEventTitle(EVENT_ID, EVENT_TEST_NAME);

        assertThat(result).isPresent();
        result.ifPresent(eventDto -> assertThat(EVENT_TEST_NAME).isEqualTo(eventDto.getName()));
    }

    @Test
    void updateEventTitleIfIdNotExist() {
        doReturn(Optional.empty()).when(eventRepository).findById(5000L);

        Optional<EventReadDto> result = eventService.updateEventTitle(5000L, EVENT_TEST_NAME);

        assertThat(result).isEmpty();
    }

    private static Event getEvent() {
        return Event.builder()
                .id(EVENT_ID)
                .name(EVENT_NAME)
                .time(TIME)
                .createdAt(LocalDateTime.of(2023, 1, 31, 23, 50))
                .build();
    }

    private static EventReadDto getReadDto() {
        return EventReadDto.builder()
                .id(EVENT_ID)
                .name(EVENT_TEST_NAME)
                .info(EVENT_TEST_INFO)
                .time(TIME)
                .build();
    }
}