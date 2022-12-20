package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;


public class StartCommand implements Command {

    private static final String START_MESSAGE = "Some welcome message...";

    private final SendServiceImpl sendServiceImpl;

    public StartCommand(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void execute(Update update) {
//        sendServiceImpl.sendMessage(update.getMessage().getFrom().getId(), START_MESSAGE);
    }
}
