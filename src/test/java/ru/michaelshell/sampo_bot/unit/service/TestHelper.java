package ru.michaelshell.sampo_bot.unit.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
class TestHelper {

    @Test
    void testDateParse() {
        LocalDateTime time = LocalDateTime.parse("2022-12-22T03:43:38.580000");
        log.info("time: {}", time);
    }
}
