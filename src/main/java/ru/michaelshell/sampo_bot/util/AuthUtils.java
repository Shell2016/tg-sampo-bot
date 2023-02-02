package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;
import org.apache.shiro.session.Session;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.session.SessionAttribute;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.AUTHENTICATED;

@UtilityClass
public class AuthUtils {

    public static boolean isAdmin(Session session) {
        return session.getAttribute(SessionAttribute.STATUS.name()).equals(Status.ADMIN.name());
    }

    public static boolean isAuthenticated(Session session) {
        return Boolean.TRUE.equals(session.getAttribute(AUTHENTICATED.name()));
    }
}
