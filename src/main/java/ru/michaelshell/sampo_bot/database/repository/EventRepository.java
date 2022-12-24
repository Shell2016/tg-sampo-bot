package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.michaelshell.sampo_bot.database.entity.Event;
import ru.michaelshell.sampo_bot.database.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {



    Optional<Event> findEventByNameAndTime(String name, LocalDateTime time);

    int deleteEventByNameAndTime(String name, LocalDateTime time);

}
