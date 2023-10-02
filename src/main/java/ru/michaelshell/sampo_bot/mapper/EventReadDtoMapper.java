package ru.michaelshell.sampo_bot.mapper;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.dto.EventReadDto;

@Component
public class EventReadDtoMapper implements Mapper<Event, EventReadDto> {

    @Override
    public EventReadDto map(Event event) {
        return EventReadDto.builder()
                .id(event.getId())
                .name(event.getName())
                .info(event.getInfo())
                .time(event.getTime())
                .build();
    }
}
