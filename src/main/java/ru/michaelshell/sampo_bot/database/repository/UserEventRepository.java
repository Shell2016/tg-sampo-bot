package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    @Query(value = "select ue from UserEvent ue " +
            "join ue.event e " +
            "join fetch ue.user u " +
            "where e.name = :eventName and e.time = :eventTime")
    List<UserEvent> findAllByNameAndTime(String eventName, LocalDateTime eventTime);

    @Query(value = """
            SELECT ue from UserEvent ue
            join ue.event e
            join fetch ue.user u
            where e.id = :id
            """)
    List<UserEvent> findAllByEventId(long id);

    Optional<UserEvent> findByUserIdAndEventNameAndEventTime(Long userId, String eventName, LocalDateTime eventTime);
}
