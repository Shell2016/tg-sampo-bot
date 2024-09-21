package ru.michaelshell.sampo_bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class EventDumpServiceLoggingAspect {

    @Pointcut("execution(void ru.michaelshell.sampo_bot.service.EventDumpService.dumpEvents())")
    public void dumpEvents() {
    }

    @AfterReturning("dumpEvents()")
    public void eventDumpLogging() {
        log.debug("Successfully dumped to google spreadsheets");
    }
}
