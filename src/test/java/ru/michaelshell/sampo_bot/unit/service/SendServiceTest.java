package ru.michaelshell.sampo_bot.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.bot.SampoBot;
import ru.michaelshell.sampo_bot.bot.SendServiceImpl;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SendServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_MESSAGE = "Test message";

    @Mock
    private SampoBot sampoBot;
    @InjectMocks
    private SendServiceImpl sendServiceImpl;


    @Test
    void sendMessage() throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(TEST_USER_ID)
                .text(TEST_MESSAGE)
                .build();

        sendServiceImpl.send(TEST_USER_ID, TEST_MESSAGE);

        verify(sampoBot).execute(sendMessage);
    }
}