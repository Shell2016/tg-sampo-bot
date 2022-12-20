package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

public class UnknownCommand implements Command {

    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";

    private final SendServiceImpl sendServiceImpl;

    public UnknownCommand(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void execute(Update update) {
//        sendServiceImpl.sendMessage(update.getMessage().getFrom().getId(), UNKNOWN_COMMAND_MESSAGE);
    }
}
