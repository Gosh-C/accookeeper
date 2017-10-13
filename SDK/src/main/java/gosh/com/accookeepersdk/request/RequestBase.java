package gosh.com.accookeepersdk.request;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by goshchan on 12/10/2017.
 */

public abstract class RequestBase extends AsyncTask<Void, Void, String>{
    protected Sheets mService;
    protected Exception mLastError = null;

    RequestBase(GoogleAccountCredential credential){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Sheets.Builder(transport, jsonFactory, credential).build();
    }

    protected List<String> getAllSheetsTitle(String spreadSheetId) throws IOException{
        List<String> results = new ArrayList<>();
        Spreadsheet response = this.mService.spreadsheets().get(spreadSheetId).execute();
        List<Sheet> sheets = response.getSheets();
        if(sheets != null){
            for(Sheet s : sheets){
                String title = s.getProperties().getTitle();
                results.add(title);
            }
        }
        else{
            throw new IOException("No sheets in Config File");
        }
        return results;
    }

    protected List<List<Object>> getDataInSheet(String spreadSheetId, String range) throws IOException{
        ValueRange response = this.mService.spreadsheets().values().get(spreadSheetId, range).execute();
        return response.getValues();
    }

    protected String appendRow(String spreadSheetId, String sheetName, List<Object> rowData) throws  IOException{
        String range = sheetName+"!A:A";
        ValueRange r = new ValueRange();
        List l = Lists.newArrayList();
        l.add(rowData);
        r.setValues(l);
        AppendValuesResponse response = mService.spreadsheets().values()
                .append(spreadSheetId, range, r).setValueInputOption("USER_ENTERED")
                .execute();
        String updated = response.getUpdates().getUpdatedRange();
        return updated;

    }
}
