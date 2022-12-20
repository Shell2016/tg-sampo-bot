package ru.michaelshell.sampo_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;
import ru.michaelshell.sampo_bot.config.BotProperties;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class SampoBot extends TelegramLongPollingSessionBot {


    private final BotProperties botProperties;
//    private final CommandContainer commands;

    public SampoBot(BotProperties botProperties) {
        this.botProperties = botProperties;
//        this.commands = new CommandContainer(new SendServiceImpl(this));
    }


    @Override
    public String getBotUsername() {
        return botProperties.username();
    }

    @Override
    public String getBotToken() {
        return botProperties.token();
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//
//            String message = update.getMessage().getText().trim();
//
//
////            if (message.startsWith("/")) {
////                String commandIdentifier = message.split(" ")[0].toLowerCase();
////                commands.getCommand(commandIdentifier).execute(update);
////            }
//
//        }
//    }
//

    @Override
    public void onUpdateReceived(Update update, Optional<Session> botSession) {

//        Subject subject = SecurityUtils.getSubject();
//
//        Session sessionFromSubject = subject.getSession(false);

        Long userId = update.getMessage().getFrom().getId();
        String textFromUser = update.getMessage().getText();
        Session session = botSession.get();
        String oldText = (String) session.getAttribute("textFromUser");
        String newText;
        if (oldText == null) {
            newText = textFromUser;
        } else {
            newText = oldText + "\n" + textFromUser;
        }

        session.setAttribute("textFromUser", newText);

        String msg = (String) session.getAttribute("textFromUser");


        sendText(userId, msg);

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
