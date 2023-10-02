package ru.michaelshell.sampo_bot.mapper;

import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.UserReadDto;

@Component
public class UserReadDtoMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User user) {
        return UserReadDto.builder()
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .build();
    }
}
