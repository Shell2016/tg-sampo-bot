package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendService;

public class SettingsCommand implements Command {

    private static final String SETTINGS_MESSAGE = "Settings ui";

    private final SendService sendService;

    public SettingsCommand(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void execute(Update update) {
        sendService.sendMessage(update.getMessage().getFrom().getId(), SETTINGS_MESSAGE);
    }
}
