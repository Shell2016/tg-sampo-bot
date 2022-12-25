package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;

import java.util.Optional;


public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    Optional<UserEvent> findUserEventByUserAndEvent(User user, Event event);


}
