package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendMessageService;

public class StartCommand implements Command {

    private static final String START_MESSAGE = "Some welcome message...";

    private final SendMessageService sendMessageService;

    public StartCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getFrom().getId(), START_MESSAGE);
    }
}
