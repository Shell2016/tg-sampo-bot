package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;
import org.apache.shiro.session.Session;
import ru.michaelshell.sampo_bot.dto.EventGetDto;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;

@UtilityClass
public class BotUtils {

    public static final String TG_NOT_SUPPORTED_CHRS_REMOVE_REGEX = "_|\\*|`";

    public static EventGetDto parseEvent(String msgText) {

        Pattern pattern = Pattern.compile("^Уровень: (.+)\\nВремя: (.+)(\\n|$)");
        Matcher matcher = pattern.matcher(msgText);
        String name = null;
        String timeString = null;
        while (matcher.find()) {
            name = matcher.group(1);
            timeString = matcher.group(2);
        }
        LocalDateTime time = null;
        if (timeString != null) {
            time = TimeParser.parseForDtoCreation(timeString);
        }
        return EventGetDto.builder()
                .name(name)
                .time(time)
                .build();
    }

    public static boolean hasRole(Session session) {
        return session.getAttribute(HAS_ROLE.name()) != null;
    }

}
