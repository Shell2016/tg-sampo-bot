package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.database.repository.UserEventRepository;
import ru.michaelshell.sampo_bot.dto.EventGetDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEventService {

    private final UserEventRepository userEventRepository;

    @Transactional
    public void deleteEventRegistration(EventGetDto eventDto, Long userId) {
        UserEvent userEvent = userEventRepository
                .findByUserIdAndEventNameAndEventTime(userId, eventDto.getName(), eventDto.getTime())
                .orElseThrow();

        userEventRepository.delete(userEvent);
    }

    public List<UserEvent> findUserEventsByEvent(EventGetDto eventGetDto) {
        return userEventRepository.findAllByNameAndTime(eventGetDto.getName(), eventGetDto.getTime());
    }

    public List<UserEvent> findAllByEventId(long id) {
        return userEventRepository.findAllByEventId(id);
    }
}
