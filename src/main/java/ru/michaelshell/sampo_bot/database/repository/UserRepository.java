package ru.michaelshell.sampo_bot.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.michaelshell.sampo_bot.database.entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    @Modifying
    @Query("update User u " +
            "set u.userName = :userName " +
            "where u.id = :id")
    int updateUserName(Long id, String userName);
}
