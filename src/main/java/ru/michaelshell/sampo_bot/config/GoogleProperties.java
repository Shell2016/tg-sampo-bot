package ru.michaelshell.sampo_bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Свойства для взаимодействия с сервисами Google.
 */
@Data
@ConfigurationProperties(prefix = "google")
public class GoogleProperties {
    private Credentials credentials;
    private Spreadsheets spreadsheets;

    @Data
    public static class Credentials {
        private String clientId;
        private String clientEmail;
        private String privateKey;
        private String privateKeyId;

        /**
         * Приведение privateKey взятого из переменных окружения к валидному PCS#8 формату.
         *
         * @return ключ в PCS#8 формате
         */
        public String getPrivateKey() {
            return privateKey
                    .replace("'", "")
                    .replace("\"", "")
                    .replace("\\n", "\n");
        }
    }

    @Data
    public static class Spreadsheets {
        private String spreadsheetId;
        private String dumpCron;
    }
}
