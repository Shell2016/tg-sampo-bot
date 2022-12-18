package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;

public class UnknownCommand implements Command {

    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";

    private final SendService sendService;

    public UnknownCommand(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void execute(Update update) {
        sendService.sendMessage(update.getMessage().getFrom().getId(), UNKNOWN_COMMAND_MESSAGE);
    }
}
