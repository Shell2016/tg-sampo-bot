package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.*;
import ru.michaelshell.sampo_bot.mapper.EventCreateDtoMapper;
import ru.michaelshell.sampo_bot.mapper.EventReadDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventReadDtoMapper eventReadDtoMapper;
    private final EventCreateDtoMapper eventCreateDtoMapper;

    public List<EventReadDto> findAllSortedByTime() {
        return eventRepository.findAll(Sort.by("time")).stream()
                .map(eventReadDtoMapper::map)
                .toList();
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Optional<EventReadDto> updateEventTitle(Long eventId, String title) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        optionalEvent.ifPresent(event -> event.setName(title));
        return optionalEvent.map(eventReadDtoMapper::map);
    }

    @Transactional
    public Optional<EventReadDto> updateEventInfo(Long eventId, String info) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        optionalEvent.ifPresent(event -> event.setInfo(info));
        return optionalEvent.map(eventReadDtoMapper::map);
    }

    @Transactional
    public Optional<EventReadDto> updateEventTime(Long eventId, LocalDateTime time) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        optionalEvent.ifPresent(event -> event.setTime(time));
        return optionalEvent.map(eventReadDtoMapper::map);
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
        return eventRepository.deleteByNameAndTime(event.getName(), event.getTime());
    }

    public Optional<Long> findEventIdByDto(EventGetDto eventDto) {
        return eventRepository.findByNameAndTime(eventDto.getName(), eventDto.getTime())
                .map(Event::getId);
    }

    public Optional<EventReadDto> findBy(EventGetDto dto) {
        return eventRepository.findByNameAndTime(dto.getName(), dto.getTime())
                .map(eventReadDtoMapper::map);
    }

}
