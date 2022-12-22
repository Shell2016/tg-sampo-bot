package ru.michaelshell.sampo_bot.mapper;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;

import java.time.LocalDateTime;

@Component
public class EventCreateDtoMapper implements Mapper<EventCreateDto, Event> {


    @Override
    public Event map(EventCreateDto dto) {
        return Event.builder()
                .name(dto.getName())
                .time(dto.getTime())
                .info(dto.getInfo())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .build();
    }
}
