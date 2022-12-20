package ru.michaelshell.sampo_bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

public class SettingsCommand implements Command {

    private static final String SETTINGS_MESSAGE = "Settings ui";

    private final SendServiceImpl sendServiceImpl;

    public SettingsCommand(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void execute(Update update) {
//        sendServiceImpl.sendMessage(update.getMessage().getFrom().getId(), SETTINGS_MESSAGE);
    }
}
