package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.michaelshell.sampo_bot.database.entity.Event;

import java.time.LocalDateTime;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByNameAndTime(String name, LocalDateTime time);

    int deleteByNameAndTime(String name, LocalDateTime time);

}
