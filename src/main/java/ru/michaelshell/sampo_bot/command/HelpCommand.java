package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;

public class HelpCommand implements Command {

    private static final String HELP_MESSAGE = """
            Help message:
              /start - start************************************
              *********
            /help - help
            /settings - settings
            """;

    private final SendService sendService;

    public HelpCommand(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void execute(Update update) {
        sendService.sendMessage(update.getMessage().getFrom().getId(), HELP_MESSAGE);
    }
}
