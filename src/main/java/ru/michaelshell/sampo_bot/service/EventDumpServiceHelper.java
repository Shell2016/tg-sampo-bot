package ru.michaelshell.sampo_bot.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.client.GoogleSpreadsheetsClient;
import ru.michaelshell.sampo_bot.config.GoogleProperties;
import ru.michaelshell.sampo_bot.exception.SheetsProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDumpServiceHelper {

    private final GoogleProperties properties;

    /**
     * Выгружает данные в лист. Если лист с данным названием уже есть, он будет заменен новым.
     *
     * @param sheetTitle - Название листа в таблице
     * @param values     - Массив строк для вставки в таблицу
     */
    public void updateSheet(String sheetTitle, List<List<Object>> values) {
        String spreadsheetId = properties.getSpreadsheets().getSpreadsheetId();
        Sheets.Spreadsheets spreadsheets = getSpreadSheets();
        try {
            Spreadsheet spreadsheet = spreadsheets.get(spreadsheetId).execute();
            BatchUpdateSpreadsheetRequest updateSpreadsheetRequest = buildUpdateSpreadsheetRequest(sheetTitle, spreadsheet);
            BatchUpdateValuesRequest updateValuesRequest = buildUpdateValuesRequest(sheetTitle, values);

            spreadsheets.batchUpdate(spreadsheetId, updateSpreadsheetRequest).execute();
            spreadsheets.values().batchUpdate(spreadsheetId, updateValuesRequest).execute();
        } catch (IOException e) {
            throw new SheetsProcessingException(e.getMessage());
        }
    }

    /**
     * Возвращает SpreadSheets для работы с Google Sheets Api.
     *
     * @return {@link Sheets.Spreadsheets}
     */
    private Sheets.Spreadsheets getSpreadSheets() {
        GoogleProperties.Credentials credentials = properties.getCredentials();
        GoogleSpreadsheetsClient spreadsheetsClient = GoogleSpreadsheetsClient.getInstance(
                credentials.getClientId(),
                credentials.getClientEmail(),
                credentials.getPrivateKey(),
                credentials.getPrivateKeyId()
        );
        return spreadsheetsClient.getSpreadsheets();
    }

    /**
     * Создает реквест для добавления листа. Если есть старый лист с таким же названием, он будет заменен.
     *
     * @param sheetTitle  Название листа
     * @param spreadsheet Ресурс представляющий гугл таблицу
     * @return реквест для добавления листа
     */
    private BatchUpdateSpreadsheetRequest buildUpdateSpreadsheetRequest(String sheetTitle, Spreadsheet spreadsheet) {
        List<Request> requests = new ArrayList<>();
        spreadsheet.getSheets().stream()
                .filter(sheet -> sheet.getProperties().getTitle().equals(sheetTitle))
                .findFirst()
                .map(sheet -> sheet.getProperties().getSheetId())
                .ifPresent(sheetId -> {
                    Request deleteSheetRequest =
                            new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId));
                    requests.add(deleteSheetRequest);
                });
        Request addSheetRequest = new Request().setAddSheet(
                new AddSheetRequest().setProperties(new SheetProperties()
                        .setTitle(sheetTitle)
                        .setIndex(0)));
        requests.add(addSheetRequest);
        return new BatchUpdateSpreadsheetRequest().setRequests(requests);
    }

    /**
     * Создает реквест для добавления данных в лист.
     *
     * @param sheetTitle Название листа в таблице
     * @param values     Данные для выгрузки
     * @return реквест для добавления данных в лист
     */
    private BatchUpdateValuesRequest buildUpdateValuesRequest(String sheetTitle, List<List<Object>> values) {
        List<ValueRange> data = new ArrayList<>();
        String range = sheetTitle + "!A1";
        data.add(new ValueRange().setRange(range).setValues(values));
        return new BatchUpdateValuesRequest().setValueInputOption("RAW").setData(data);
    }
}
