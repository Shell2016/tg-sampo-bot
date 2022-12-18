package ru.michaelshell.sampo_bot.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.sampo_bot.service.SendService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommandContainerTest {

    @Mock
    private SendService sendService;
    private final CommandContainer commandContainer = new CommandContainer(sendService);

    @Test
    void shouldHaveAllSupportedCommands() {

        Arrays.stream(CommandName.values())
                .forEach(commandName ->
                        assertThat(commandContainer.getCommand(commandName.getCommandName()).getClass()).isNotEqualTo(UnknownCommand.class));
    }

    @Test
    void shouldReturnUnknownCommand() {
        String unknownCommand = "/testCommand";

        assertThat(commandContainer.getCommand(unknownCommand).getClass()).isEqualTo(UnknownCommand.class);
    }
}