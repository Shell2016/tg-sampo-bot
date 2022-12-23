import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {

    public static void main(String[] args) {
        String dateString = "01 января 10:0022";

//        boolean matches = toValidate.matches("([0-2][0-9]|3[0-1]) (0[1-9]|1[0-2]) ([0-1][0-9]|2[0-3]):[0-5][0-9]");
//        System.out.println(matches);


//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mmyy", new Locale("ru"));
//
//        LocalDateTime time = LocalDateTime.parse(dateString, formatter);
//        System.out.println(time);

        String msgText = """
                Уровень: Tsk  ljd - dfdf
                Время: 20 декабря 2020 10:30          
                """;

        Matcher matcher = Pattern.compile("^Уровень: (.+)\\nВремя: (.+)$").matcher(msgText);
        while (matcher.find()) {
            System.out.println(matcher.group());

        }

    }
}
