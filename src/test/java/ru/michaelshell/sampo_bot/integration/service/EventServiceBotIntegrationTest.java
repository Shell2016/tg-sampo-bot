package ru.michaelshell.sampo_bot.integration.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.integration.IntegrationTestBase;
import ru.michaelshell.sampo_bot.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class EventServiceBotIntegrationTest extends IntegrationTestBase {

    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "RS-Main";
    private static final String EVENT_TEST_NAME = "testTitle";
    private static final LocalDateTime EVENT_TIME = LocalDateTime.of(2023, 05, 10, 20, 30);

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
    void updateEventTitleIfWrongId() {
        Optional<EventReadDto> result = eventService.updateEventTitle(1000L, EVENT_TEST_NAME);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateInfoTest")
    void updateEventInfo(Long eventId, String info, Optional<EventReadDto> expected) {
        Optional<EventReadDto> result = eventService.updateEventInfo(eventId, info);

        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForUpdateInfoTest() {
        return Stream.of(
                Arguments.of(100L, "dummyInfo", Optional.empty()),
                Arguments.of(3L, "dummyInfo", Optional.of(
                        EventReadDto.builder()
                                .id(3L)
                                .info("dummyInfo")
                                .name("Star")
                                .time(LocalDateTime.parse("2022-05-22T03:43:00.000000"))
                                .build()
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateEventTimeTest")
    void updateEventTime(Long eventId, LocalDateTime time, Optional<EventReadDto> expected) {
        Optional<EventReadDto> result = eventService.updateEventTime(eventId, time);

        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForUpdateEventTimeTest() {
        return Stream.of(
                Arguments.of(100L, EVENT_TIME, Optional.empty()),
                Arguments.of(3L, EVENT_TIME, Optional.of(
                        EventReadDto.builder()
                                .id(3L)
                                .info("")
                                .name("Star")
                                .time(EVENT_TIME)
                                .build()
                ))
        );
    }

    @Test
    void findAll() {
        List<EventReadDto> result = eventService.findAllSortedByTime();

        assertThat(result).hasSize(3);
    }


    @Test
    void create() {
        EventCreateDto dto = EventCreateDto.builder()
                .name("CH")
                .time(EVENT_TIME)
                .createdAt(EVENT_TIME)
                .createdBy("shell_2017")
                .build();
        EventReadDto expected = EventReadDto.builder()
                .name("CH")
                .time(EVENT_TIME)
                .id(4L)
                .build();

        EventReadDto result = eventService.create(dto);

        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForDeleteTest")
    void delete(EventGetDto dto, int expected) {
        int result = eventService.delete(dto);

        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForDeleteTest() {

        return Stream.of(
                Arguments.of(EventGetDto.builder()
                        .name("RS-Main")
                        .time(EVENT_TIME)
                        .build(), 0),
                Arguments.of(EventGetDto.builder()
                        .name("dummy")
                        .time(LocalDateTime.parse("2022-12-22T03:43:00.000000"))
                        .build(), 0),
                Arguments.of(EventGetDto.builder()
                        .name("RS-Main")
                        .time(LocalDateTime.parse("2022-12-22T03:43:00.000000"))
                        .build(), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindEventIdByDtoTest")
    void findEventIdByDto(EventGetDto dto, Optional<Long> expected) {
        Optional<Long> result = eventService.findEventIdByDto(dto);

        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForFindEventIdByDtoTest() {
        return Stream.of(
                Arguments.of(EventGetDto.builder()
                        .name("RS-Main")
                        .time(EVENT_TIME)
                        .build(), Optional.empty()),
                Arguments.of(EventGetDto.builder()
                        .name("RS-Main")
                        .time(LocalDateTime.parse("2022-12-22T03:43:00.000000"))
                        .build(), Optional.of(1L))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindByTest")
    void findBy(EventGetDto dto, Optional<EventReadDto> expected) {
        Optional<EventReadDto> result = eventService.findBy(dto);

        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForFindByTest() {
        LocalDateTime time = LocalDateTime.parse("2022-05-22T03:43:00.000000");
        return Stream.of(
                Arguments.of(EventGetDto.builder()
                        .name("RS-Main")
                        .time(EVENT_TIME)
                        .build(), Optional.empty()),
                Arguments.of(EventGetDto.builder()
                                .name("Star")
                                .time(time)
                                .build(),
                        Optional.of(EventReadDto.builder()
                                .id(3L)
                                .info("")
                                .name("Star")
                                .time(time)
                                .build()))
        );
    }

}
