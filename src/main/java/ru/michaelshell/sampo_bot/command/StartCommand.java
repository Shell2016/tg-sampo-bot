package ru.michaelshell.sampo_bot.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;



public class StartCommand implements Command {

    private static final String START_MESSAGE = "Some welcome message...";

    private final SendService sendService;

    public StartCommand(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void execute(Update update) {
        sendService.sendMessage(update.getMessage().getFrom().getId(), START_MESSAGE);
    }
}
