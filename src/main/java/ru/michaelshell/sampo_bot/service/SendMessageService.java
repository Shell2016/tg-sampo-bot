package ru.michaelshell.sampo_bot.service;

public interface SendMessageService {

    void sendMessage(Long userId, String message);
}
