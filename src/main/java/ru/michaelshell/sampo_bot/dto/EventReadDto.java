package ru.michaelshell.sampo_bot.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class EventReadDto {

    String name;
    String info;
    LocalDateTime time;
}
