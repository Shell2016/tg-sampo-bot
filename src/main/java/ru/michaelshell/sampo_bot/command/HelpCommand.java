package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendMessageService;

public class HelpCommand implements Command {

    private static final String HELP_MESSAGE = """
            Help message:
              /start - start************************************
              *********
            /help - help
            /settings - settings
            """;

    private final SendMessageService sendMessageService;

    public HelpCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getFrom().getId(), HELP_MESSAGE);
    }
}
