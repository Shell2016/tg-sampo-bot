package ru.michaelshell.sampo_bot.bot.exception;

public class TelegramResponseException extends RuntimeException {
    public TelegramResponseException(Throwable cause) {
        super(cause);
    }
}
