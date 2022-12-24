package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.mapper.EventCreateDtoMapper;
import ru.michaelshell.sampo_bot.mapper.EventReadDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventReadDtoMapper eventReadDtoMapper;
    private final EventCreateDtoMapper eventCreateDtoMapper;

    public List<EventReadDto> findAll() {

        return eventRepository.findAll(Sort.by("time")).stream()
                .map(eventReadDtoMapper::map)
                .collect(toList());
    }

    @Transactional
    public EventReadDto create(EventCreateDto dto) {
        return Optional.of(dto)
                .map(eventCreateDtoMapper::map)
                .map(eventRepository::save)
                .map(eventReadDtoMapper::map)
                .orElseThrow();
    }

    @Transactional
    public int delete(EventGetDto event) {
        return eventRepository.deleteEventByNameAndTime(event.getName(), event.getTime());
    }

    public Optional<Long> findIdByDto(EventGetDto eventDto) {
        return eventRepository.findEventByNameAndTime(eventDto.getName(), eventDto.getTime())
                .map(Event::getId);
    }



}
