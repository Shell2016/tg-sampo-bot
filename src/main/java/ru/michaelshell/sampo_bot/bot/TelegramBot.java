package ru.michaelshell.sampo_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.michaelshell.sampo_bot.command.CommandContainer;
import ru.michaelshell.sampo_bot.config.TelegramBotProperties;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String HELP_TEXT = """
            Some help text
            /start - blablabla
            /help - blabla bla
            """;

    private final TelegramBotProperties telegramBotProperties;
    private final CommandContainer commands;

    public TelegramBot(TelegramBotProperties telegramBotProperties) {
        this.telegramBotProperties = telegramBotProperties;
        this.commands = new CommandContainer(new SendServiceImpl(this));
    }


    @Override
    public String getBotUsername() {
        return telegramBotProperties.username();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.token();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (message.startsWith("/")) {
                String commandIdentifier = message.split(" ")[0].toLowerCase();
                commands.getCommand(commandIdentifier).execute(update);
            }

        }






    }




    public void sendText(Long userId, String msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId) //Who are we sending a message to
                .text(msg).build();    //Message content
        try {
            execute(sendMessage);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

//    private void copyMessage(Long userId, Integer msgId) {
//        CopyMessage copyMessage = CopyMessage.builder()
//                .fromChatId(userId)
//                .chatId(userId)
//                .messageId(msgId)
//                .build();
//        try {
//            execute(copyMessage);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void sendMenu(Long userId, String txt, InlineKeyboardMarkup keyBoard){
//        SendMessage sm = SendMessage.builder().chatId(userId.toString())
//                .parseMode("HTML").text(txt)
//                .replyMarkup(keyBoard).build();
//
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
