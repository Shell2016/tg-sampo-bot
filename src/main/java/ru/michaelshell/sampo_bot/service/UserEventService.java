package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;
import ru.michaelshell.sampo_bot.dto.EventGetDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEventService {

    private final UserEventRepository userEventRepository;

    @Transactional
    public void deleteEventRegistration(EventGetDto eventDto, Long userId) {
        UserEvent userEvent = userEventRepository
                .findUserEventByUserIdAndEventNameAndEventTime(userId, eventDto.getName(), eventDto.getTime())
                .orElseThrow();

        userEventRepository.delete(userEvent);
    }


    public List<UserEvent> findUserEventsByEvent(EventGetDto eventGetDto) {
        return userEventRepository.findUserEventsByEventNameAndEventTime(eventGetDto.getName(), eventGetDto.getTime());
    }
}
