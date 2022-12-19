package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.sampo_bot.database.entity.User;

import java.util.Optional;

//@Transactional
public interface UserRepository extends Repository<User, Long> {

    Optional<User> findById(Long userId);

    User save(User user);

    void deleteById(Long userId);
}
