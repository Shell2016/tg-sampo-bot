package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendMessageService;

public class SettingsCommand implements Command {

    private static final String SETTINGS_MESSAGE = "Settings ui";

    private final SendMessageService sendMessageService;

    public SettingsCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getFrom().getId(), SETTINGS_MESSAGE);
    }
}
