package ru.michaelshell.sampo_bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.dto.EventCreateDto;
import ru.michaelshell.sampo_bot.dto.EventGetDto;

@Slf4j
@Aspect
@Component
public class EventServiceLoggingAspect {

    @Pointcut("execution(ru.michaelshell.sampo_bot.dto.EventReadDto ru.michaelshell.sampo_bot.service.EventService.create(ru.michaelshell.sampo_bot.dto.EventCreateDto)) java.util.NoSuchElementException)")
    public void createEventMethod() {
    }

    @Pointcut("execution(int ru.michaelshell.sampo_bot.service.EventService.delete(ru.michaelshell.sampo_bot.dto.EventGetDto))")
    public void deleteEventMethod() {
    }

    @AfterThrowing("createEventMethod()")
    public void createEventErrorLogging() {
        log.info("Event creation error");
    }

    @AfterReturning("createEventMethod() && args(eventDto)")
    public void createEventLogging(EventCreateDto eventDto) {
        log.info("Event created {}", eventDto);
    }

    @AfterReturning(value = "deleteEventMethod() &&args(eventDto)", returning = "result", argNames = "result,eventDto")
    public void deleteEventLogging(int result, EventGetDto eventDto) {
        if (result == 1) {
            log.info("Event deleted {}", eventDto);
        } else {
            log.info("Event deleting error {}", eventDto);
        }
    }
}
