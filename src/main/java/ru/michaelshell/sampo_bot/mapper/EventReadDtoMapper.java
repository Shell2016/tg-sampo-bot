package ru.michaelshell.sampo_bot.mapper;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.dto.UserReadDto;

@Component
public class EventReadDtoMapper implements Mapper<Event, EventReadDto> {

    @Override
    public EventReadDto map(Event event) {

        return EventReadDto.builder()
                .name(event.getName())
                .info(event.getInfo())
                .time(event.getTime())
                .build();
    }
}
