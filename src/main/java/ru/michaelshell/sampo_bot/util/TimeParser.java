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
    private static final String PATTERN_FOR_DTO_CREATION = "dd MMMM yyyy  HH:mm";

    public static LocalDateTime parseForEventCreation(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FOR_EVENT_CREATION);
        return LocalDateTime.parse(timeString, formatter);
    }

    public static LocalDateTime parseForDtoCreation(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FOR_DTO_CREATION, new Locale("ru"));
        return LocalDateTime.parse(timeString, formatter);
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

//    private boolean regexValidateDate(String eventDate) {
//        return eventDate.matches("([0-2][0-9]|3[0-1]) (0[1-9]|1[0-2]) 2[2-9] ([0-1][0-9]|2[0-3]):[0-5][0-9]");
//    }
}
