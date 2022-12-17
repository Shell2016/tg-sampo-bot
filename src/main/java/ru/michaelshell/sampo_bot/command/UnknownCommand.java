package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendMessageService;

public class UnknownCommand implements Command {

    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";

    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getFrom().getId(), UNKNOWN_COMMAND_MESSAGE);
    }
}
