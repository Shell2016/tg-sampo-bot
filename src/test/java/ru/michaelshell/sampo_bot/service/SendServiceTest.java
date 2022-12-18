package ru.michaelshell.sampo_bot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.TelegramBot;


@ExtendWith(MockitoExtension.class)
class SendServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_MESSAGE = "Test message";

    @Mock
    private TelegramBot telegramBot;
    @InjectMocks
    private SendServiceImpl sendService;


    @Test
    void sendMessage() throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(TEST_USER_ID)
                .text(TEST_MESSAGE)
                .build();

        sendService.sendMessage(TEST_USER_ID, TEST_MESSAGE);

        Mockito.verify(telegramBot).execute(sendMessage);
    }
}