package ru.michaelshell.sampo_bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserEventServiceLoggingAspect {

    @Pointcut("execution(void ru.michaelshell.sampo_bot.service.UserEventService.deleteEventRegistration(..))")
    public void deleteEventRegistrationMethod() {
    }

    @After(value = "deleteEventRegistrationMethod() && args(eventDto, userId)", argNames = "eventDto,userId")
    public void deleteEventRegistrationLogging(Object eventDto, Object userId) {
        log.info("DeleteEventRegistration method invoked by user {} with {}", userId, eventDto);
    }

}
