package ru.michaelshell.sampo_bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;

@Slf4j
@Aspect
@Component
public class UserServiceLoggingAspect {

    @Pointcut("execution(* ru.michaelshell.sampo_bot.service.UserService.createUser(*)) *")
    public void createUserMethod() {
    }

    @Pointcut("execution(void ru.michaelshell.sampo_bot.service.UserService.promoteByUserName(java.lang.String)) *")
    public void promoteUserMethod() {
    }

    @Pointcut("execution(* ru.michaelshell.sampo_bot.service.UserService.setUserRole(..))")
    public void setUserRoleMethod() {
    }

    @Pointcut("execution(* ru.michaelshell.sampo_bot.service.UserService.registerOnEvent(..))")
    public void registerOnEventMethod() {
    }


    @AfterThrowing("createUserMethod() && args(userDto)")
    public void createUserErrorLogging(Object userDto) {
        log.info("User creation error! {}", userDto);
    }

    @AfterReturning(value = "createUserMethod()", returning = "result")
    public void createUserLogging(Object result) {
        log.info("User created {}", result);
    }

    @AfterThrowing("promoteUserMethod() && args(userName)")
    public void promoteUserErrorLogging(String userName) {
        log.info("User for promotion not found: {}", userName);
    }

    @AfterReturning("promoteUserMethod() && args(userName)")
    public void promoteUserLogging(String userName) {
        log.info("User promoted: {}", userName);
    }

    @AfterReturning(value = "setUserRoleMethod()", returning = "result")
    public void setUserRoleLogging(Object result) {
        log.info("Method setUserRole invoked. Result: {}", result);
    }

    @AfterReturning(value = "registerOnEventMethod()", returning = "result")
    public void registerOnEventLogging(UserEvent result) {
        log.info("RegisterOnEvent method returned: {} {} {}", result, result.getUser(), result.getEvent());
    }

}
