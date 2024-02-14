package ru.michaelshell.sampo_bot.client;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.Getter;
import ru.michaelshell.sampo_bot.exception.GoogleSheetsClientException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Thread-safe singleton with lazy initialization for Google Sheets Api client.
 */
public final class GoogleSpreadsheetsClient {

    private static GoogleSpreadsheetsClient instance;
    /**
     * Нужен для работы с Google Spreadsheets Api.
     */
    @Getter
    private final Sheets.Spreadsheets spreadsheets;

    private GoogleSpreadsheetsClient(String clientId, String clientEmail, String privateKey, String privateKeyId) {
        this.spreadsheets = getSpreadsheets(clientId, clientEmail, privateKey, privateKeyId);
    }

    /**
     * Синхронизированный метод для получения инстанса класса.
     *
     * @param clientId     google clientId
     * @param clientEmail  google client-email
     * @param privateKey   google service account private key
     * @param privateKeyId google privateKeyId
     * @return {@link GoogleSpreadsheetsClient}
     */
    public static synchronized GoogleSpreadsheetsClient getInstance(String clientId,
                                                                    String clientEmail,
                                                                    String privateKey,
                                                                    String privateKeyId) {
        if (instance == null) {
            instance = new GoogleSpreadsheetsClient(clientId, clientEmail, privateKey, privateKeyId);
        }
        return instance;
    }

    /**
     * Парсит credentials для  работы с Google Sheets и создает объект SpreadSheets для работы с Google Sheets Api.
     *
     * @return {@link Sheets.Spreadsheets}
     */
    private Sheets.Spreadsheets getSpreadsheets(String clientId,
                                                String clientEmail,
                                                String privateKey,
                                                String privateKeyId) {
        ServiceAccountCredentials credentials;
        NetHttpTransport transport;
        try {
            credentials = ServiceAccountCredentials.fromPkcs8(
                    clientId,
                    clientEmail,
                    privateKey,
                    privateKeyId,
                    Collections.singleton(SheetsScopes.SPREADSHEETS)
            );
            transport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleSheetsClientException(e.getMessage());
        }
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);
        return new Sheets.Builder(transport, GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("sampobot")
                .build()
                .spreadsheets();
    }
}
