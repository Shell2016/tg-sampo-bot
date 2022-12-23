package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;


public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

}
