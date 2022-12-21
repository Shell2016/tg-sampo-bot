package ru.michaelshell.sampo_bot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;

import java.time.LocalDateTime;

@Value
@Builder
public class UserReadDto {

    String userName;
    String firstName;
    String lastName;
    Role role;
    Status status;
    LocalDateTime registeredAt;
}
