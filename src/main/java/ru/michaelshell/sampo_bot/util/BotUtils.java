package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;
import org.apache.shiro.session.Session;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.session.SessionAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.AUTHENTICATED;

@UtilityClass
public class BotUtils {

    public static final String TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX = "_|\\*|`";

    public static boolean isAdmin(Session session) {
        return session.getAttribute(SessionAttribute.STATUS.name()).equals(Status.ADMIN.name());
    }

    public static boolean isAuthenticated(Session session) {
        return Boolean.TRUE.equals(session.getAttribute(AUTHENTICATED.name()));
    }


    public static EventGetDto parseEvent(String msgText) {

        Pattern pattern = Pattern.compile("^Уровень: (.+)\\nВремя: (.+)(\\n|$)");
        Matcher matcher = pattern.matcher(msgText);
        String name = null;
        String timeString = null;
        while (matcher.find()) {
            name = matcher.group(1);
            timeString = matcher.group(2);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy  HH:mm", new Locale("ru"));
        LocalDateTime time = LocalDateTime.parse(timeString, formatter);
        return EventGetDto.builder()
                .name(name)
                .time(time)
                .build();
    }
}
