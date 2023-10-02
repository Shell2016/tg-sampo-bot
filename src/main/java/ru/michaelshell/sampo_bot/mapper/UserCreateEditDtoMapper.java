package ru.michaelshell.sampo_bot.mapper;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.UserCreateEditDto;

@Component
public class UserCreateEditDtoMapper implements Mapper<UserCreateEditDto, User> {

    @Override
    public User map(UserCreateEditDto dto) {
        return User.builder()
                .id(dto.getId())
                .userName(dto.getUserName())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .status(dto.getStatus())
                .registeredAt(dto.getRegisteredAt())
                .build();
    }
}
