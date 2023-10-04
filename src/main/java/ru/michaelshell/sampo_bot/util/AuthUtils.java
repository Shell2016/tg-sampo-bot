package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.session.UserSession;


@UtilityClass
public class AuthUtils {

    public static boolean isAdmin(UserSession session) {
        return session.getUserStatus() != null && session.getUserStatus() == Status.ADMIN;
    }
}
