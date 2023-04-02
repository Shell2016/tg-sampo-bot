package ru.michaelshell.sampo_bot.dto;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;

import java.time.LocalDateTime;

@Value
@FieldNameConstants
@Builder
public class UserCreateEditDto {

    Long id;

    String userName;

    String firstName;

    String lastName;

    Role role;

    Status status;

    LocalDateTime registeredAt;
}
