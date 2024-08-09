package ru.michaelshell.sampo_bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.michaelshell.sampo_bot.database.entity.Status;
import ru.michaelshell.sampo_bot.session.UserSession;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminKeyboard;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListKeyboard;


@UtilityClass
public class AuthUtils {

    public static ReplyKeyboard getBottomKeyboard(UserSession session) {
        return AuthUtils.isAdmin(session) ? eventListAdminKeyboard : eventListKeyboard;
    }

    public static InlineKeyboardMarkup getInlineKeyboard(UserSession session) {
        return AuthUtils.isAdmin(session) ? eventListAdminButtons : eventListButtons;
    }

    public static boolean isAdmin(UserSession session) {
        return session.getUserStatus() != null && session.getUserStatus() == Status.ADMIN;
    }
}
