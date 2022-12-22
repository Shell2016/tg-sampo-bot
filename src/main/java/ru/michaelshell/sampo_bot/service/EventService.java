package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.repository.EventRepository;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.mapper.EventReadDtoMapper;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventReadDtoMapper eventReadDtoMapper;

    public List<EventReadDto> findAll() {
        return eventRepository.findAll().stream()
                .map(eventReadDtoMapper::map)
                .collect(toList());
    }


}
