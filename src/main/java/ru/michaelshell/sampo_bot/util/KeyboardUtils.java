package ru.michaelshell.sampo_bot.util;

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

    public static final ReplyKeyboardMarkup eventListKeyboard = KeyboardUtils.getEventListKeyboard();
    public static final ReplyKeyboardMarkup eventListAdminKeyboard = KeyboardUtils.getEventListAdminKeyboard();
    public static final InlineKeyboardMarkup eventListButtons = KeyboardUtils.getEventListButtons();
    public static final InlineKeyboardMarkup eventListAdminButtons = KeyboardUtils.getEventListAdminButtons();
    public static final InlineKeyboardMarkup eventListAdminButtonsDeleteConfirmation = KeyboardUtils.getEventListAdminButtonsDeleteConfirmation();
    public static final InlineKeyboardMarkup eventInfoButtons = KeyboardUtils.getEventInfoButtons();
    public static final InlineKeyboardMarkup roleSelectButtons = KeyboardUtils.getRoleSelectButtons();
    public static final InlineKeyboardMarkup registerEventModeButtons = KeyboardUtils.getRegisterEventModeButtons();
    public static final InlineKeyboardMarkup eventRegisterButton = KeyboardUtils.getEventRegisterButton();
    public static final InlineKeyboardMarkup deleteRegistrationButton = KeyboardUtils.getDeleteRegistrationButton();
    public static final InlineKeyboardMarkup eventEditButtons = KeyboardUtils.getEventEditButtons();

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

    private static ReplyKeyboardMarkup getEventListKeyboard() {
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

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonList);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);

        eventListButtons.setKeyboard(rowList);
        return eventListButtons;
    }

    private static InlineKeyboardMarkup getEventListAdminButtons() {
        InlineKeyboardMarkup eventListAdminButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonList = new InlineKeyboardButton();
        buttonList.setText("Списки");
        buttonList.setCallbackData("buttonShowDancersList");

        InlineKeyboardButton buttonDelete = new InlineKeyboardButton();
        buttonDelete.setText("Удалить");
        buttonDelete.setCallbackData("buttonEventDelete");

        InlineKeyboardButton buttonEventEdit = new InlineKeyboardButton();
        buttonEventEdit.setText("Редактировать");
        buttonEventEdit.setCallbackData("buttonEventEdit");

        InlineKeyboardButton buttonEventNotify = new InlineKeyboardButton();
        buttonEventNotify.setText("\uD83D\uDCE3 Оповещение \uD83D\uDCE3");
        buttonEventNotify.setCallbackData("buttonEventNotify");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonList);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonEventEdit);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(buttonDelete);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(buttonEventNotify);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);
        rowList.add(row4);

        eventListAdminButtons.setKeyboard(rowList);
        return eventListAdminButtons;
    }

    private static InlineKeyboardMarkup getEventListAdminButtonsDeleteConfirmation() {
        InlineKeyboardMarkup eventListAdminButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonList = new InlineKeyboardButton();
        buttonList.setText("Списки");
        buttonList.setCallbackData("buttonShowDancersList");

        InlineKeyboardButton buttonDelete = new InlineKeyboardButton();
        buttonDelete.setText("❗️️ПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ❗️️");
        buttonDelete.setCallbackData("buttonEventDeleteConfirmation");


        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonList);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonDelete);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);

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

    private static InlineKeyboardMarkup getRoleSelectButtons() {
        InlineKeyboardMarkup roleSelectButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonLeader = new InlineKeyboardButton();
        buttonLeader.setText("Партнёр");
        buttonLeader.setCallbackData("buttonLeader");

        InlineKeyboardButton buttonFollower = new InlineKeyboardButton();
        buttonFollower.setText("Партнёрша");
        buttonFollower.setCallbackData("buttonFollower");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonLeader);
        row.add(buttonFollower);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);

        roleSelectButtons.setKeyboard(rowList);
        return roleSelectButtons;
    }

    private static InlineKeyboardMarkup getRegisterEventModeButtons() {
        InlineKeyboardMarkup registerEventModeButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonSolo = new InlineKeyboardButton();
        buttonSolo.setText("Без пары");
        buttonSolo.setCallbackData("buttonSolo");

        InlineKeyboardButton buttonCouple = new InlineKeyboardButton();
        buttonCouple.setText("В паре");
        buttonCouple.setCallbackData("buttonCouple");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSolo);
        row.add(buttonCouple);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);

        registerEventModeButtons.setKeyboard(rowList);
        return registerEventModeButtons;
    }

    private static InlineKeyboardMarkup getEventRegisterButton() {
        InlineKeyboardMarkup eventRegisterButton = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonRegister = new InlineKeyboardButton();
        buttonRegister.setText("Записаться");
        buttonRegister.setCallbackData("buttonEventRegister");

        InlineKeyboardButton buttonRefresh = new InlineKeyboardButton();
        buttonRefresh.setText("Обновить списки");
        buttonRefresh.setCallbackData("buttonRefresh");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonRegister);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonRefresh);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);

        eventRegisterButton.setKeyboard(rowList);
        return eventRegisterButton;
    }

    private static InlineKeyboardMarkup getDeleteRegistrationButton() {
        InlineKeyboardMarkup deleteRegistrationButton = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonDeleteRegistration = new InlineKeyboardButton();
        buttonDeleteRegistration.setText("Удалить запись");
        buttonDeleteRegistration.setCallbackData("buttonDeleteRegistration");

        InlineKeyboardButton buttonRefresh = new InlineKeyboardButton();
        buttonRefresh.setText("Обновить списки");
        buttonRefresh.setCallbackData("buttonRefresh");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonDeleteRegistration);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonRefresh);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);

        deleteRegistrationButton.setKeyboard(rowList);
        return deleteRegistrationButton;
    }

    private static InlineKeyboardMarkup getEventEditButtons() {
        InlineKeyboardMarkup eventEditButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonEditEventTitle = new InlineKeyboardButton();
        buttonEditEventTitle.setText("Уровень");
        buttonEditEventTitle.setCallbackData("buttonEditEventTitle");

        InlineKeyboardButton buttonEditEventTime = new InlineKeyboardButton();
        buttonEditEventTime.setText("Время");
        buttonEditEventTime.setCallbackData("buttonEditEventTime");

        InlineKeyboardButton buttonEditEventInfo = new InlineKeyboardButton();
        buttonEditEventInfo.setText("Инфо");
        buttonEditEventInfo.setCallbackData("buttonEditEventInfo");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonEditEventTitle);
        row1.add(buttonEditEventTime);
        row1.add(buttonEditEventInfo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);

        eventEditButtons.setKeyboard(rowList);
        return eventEditButtons;
    }

}
