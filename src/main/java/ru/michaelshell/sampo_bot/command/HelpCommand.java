package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

public class HelpCommand implements Command {

    private static final String HELP_MESSAGE = """
            Help message:
              /start - start************************************
              *********
            /help - help
            /settings - settings
            """;

    private final SendServiceImpl sendServiceImpl;

    public HelpCommand(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void execute(Update update) {
//        sendServiceImpl.sendMessage(update.getMessage().getFrom().getId(), HELP_MESSAGE);
    }
}
