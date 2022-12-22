import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class TestClass {

    public static void main(String[] args) {
        String toValidate = "20 12 20:30";

        boolean matches = toValidate.matches("([0-2][0-9]|3[0-1]) (0[1-9]|1[0-2]) ([0-1][0-9]|2[0-3]):[0-5][0-9]");
        System.out.println(matches);



        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM HH:mmyyyy");
        LocalDateTime time = LocalDateTime.parse(toValidate + LocalDate.now().getYear(), formatter);
        System.out.println(time);
    }
}
