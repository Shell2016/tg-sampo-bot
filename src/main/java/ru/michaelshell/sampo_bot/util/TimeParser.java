package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class TimeParser {

    private static final String PATTERN_FOR_EVENT_CREATION = "dd MM yy HH:mm";
    private static final String PATTERN = "dd MMMM yyyy  HH:mm";
    private static final DateTimeFormatter FORMATTER_FOR_EVENT_CREATION = DateTimeFormatter.ofPattern(PATTERN_FOR_EVENT_CREATION);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN, new Locale("ru"));

    public static LocalDateTime parseForEventCreation(String timeString) {
        return LocalDateTime.parse(timeString, FORMATTER_FOR_EVENT_CREATION);
    }

    public static LocalDateTime parseForDtoCreation(String timeString) {
        return LocalDateTime.parse(timeString, FORMATTER);
    }

    public static String parseFromTimeToString(LocalDateTime time) {
        return time.format(FORMATTER);
    }

    public static boolean isValid(String timeString) {
        try {
            return Optional.ofNullable(timeString)
                    .map(TimeParser::parseForEventCreation)
                    .isPresent();
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
