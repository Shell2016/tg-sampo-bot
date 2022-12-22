package ru.michaelshell.sampo_bot.keyboard;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class KeyboardUtils {

    public final static ReplyKeyboardMarkup eventListKeyboard = KeyboardUtils.getEventListKeyboard();
    public final static ReplyKeyboardMarkup eventListAdminKeyboard = KeyboardUtils.getEventListAdminKeyboard();
    public final static InlineKeyboardMarkup eventListButtons = KeyboardUtils.getEventListButtons();
    public final static InlineKeyboardMarkup eventListAdminButtons = KeyboardUtils.getEventListAdminButtons();
    public final static InlineKeyboardMarkup eventInfoButtons = KeyboardUtils.getEventInfoButtons();



    private static ReplyKeyboardMarkup getEventListAdminKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Список коллективок"));
        row.add(new KeyboardButton("Добавить"));
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getEventListKeyboard () {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Список коллективок"));
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private static InlineKeyboardMarkup getEventListButtons() {
        InlineKeyboardMarkup eventListButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonList = new InlineKeyboardButton();
        buttonList.setText("Списки");
        buttonList.setCallbackData("buttonShowDancersList");

        InlineKeyboardButton buttonRegister = new InlineKeyboardButton();
        buttonRegister.setText("Записаться");
        buttonRegister.setCallbackData("buttonEventRegister");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonList);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonRegister);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);

        eventListButtons.setKeyboard(rowList);
        return eventListButtons;
    }

    private static InlineKeyboardMarkup getEventListAdminButtons() {
        InlineKeyboardMarkup eventListAdminButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonList = new InlineKeyboardButton();
        buttonList.setText("Списки");
        buttonList.setCallbackData("buttonShowDancersList");

        InlineKeyboardButton buttonRegister = new InlineKeyboardButton();
        buttonRegister.setText("Записаться");
        buttonRegister.setCallbackData("buttonEventRegister");

        InlineKeyboardButton buttonDelete = new InlineKeyboardButton();
        buttonDelete.setText("Удалить");
        buttonDelete.setCallbackData("buttonEventDelete");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonList);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonRegister);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(buttonDelete);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);

        eventListAdminButtons.setKeyboard(rowList);
        return eventListAdminButtons;
    }

    private static InlineKeyboardMarkup getEventInfoButtons() {
        InlineKeyboardMarkup eventListButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonInfoYes = new InlineKeyboardButton();
        buttonInfoYes.setText("Да");
        buttonInfoYes.setCallbackData("buttonInfoYes");

        InlineKeyboardButton buttonInfoNo = new InlineKeyboardButton();
        buttonInfoNo.setText("Нет");
        buttonInfoNo.setCallbackData("buttonInfoNo");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonInfoYes);
        row1.add(buttonInfoNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);

        eventListButtons.setKeyboard(rowList);
        return eventListButtons;
    }
}
