package gosh.com.accookeepersdk.request;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.io.IOException;
import java.util.List;

import gosh.com.accookeepersdk.utils.PrefUtils;

/**
 * Created by goshchan on 12/10/2017.
 */

public class AppConfigRequest extends RequestBase {
    private Context mContext;
    private RequestCallback mCB;

    public AppConfigRequest(GoogleAccountCredential credential, Context context, RequestCallback cb) {
        super(credential);
        mContext = context;
        mCB = cb;
    }

    /**
     * Fetch a list of names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     * @return List of names and majors
     * @throws IOException
     */
    private String getDataFromApi() throws IOException {
        String spreadSheetId = PrefUtils.getAppConfigResourceId(mContext);
        List<String> titles = getAllSheetsTitle(spreadSheetId);
        StringBuilder sb = new StringBuilder();
        if(titles != null && titles.size() > 0){
            for(String title : titles){
                sb.append(title).append("\n");
                List<List<Object>> values = getDataInSheet(spreadSheetId, title);
                if(values != null && values.size() > 0){
                    for (List row : values) {
                        sb.append("\t").append(row.get(0));
                        sb.append("\t").append(row.get(1));
                        sb.append("\t").append(row.get(2));
                        sb.append("\n");
                    }
                }
            }
        }
        return sb.toString();

//        String range = "Config:A1|A";
//        List<String> results = new ArrayList<>();
////        ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
//        Spreadsheet response = this.mService.spreadsheets().get(spreadsheetId).execute();
//        List<Sheet> sheets = response.getSheets();
//        if(sheets != null){
//            for(Sheet s : sheets){
//                String title = s.getProperties().getTitle();
//                results.add(title);
//            }
//        }
//        else{
//            throw new IOException("No sheets in Config File");
//        }
//        return results;
//
//
//        List<List<Object>> values = response.getValues();
//        if (values != null) {
//            for (List row : values) {
//                results.add(row.get(0) + "" );
//            }
//        }
//        return results;
    }

    @Override
    protected void onPostExecute(String output) {
        if (output == null) {
            showError(mLastError == null? null : mLastError.toString());
        } else {
            mCB.onFinishedFetchData(output);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return mLastError.toString();
        }
    }

    @Override
    protected void onCancelled(String s) {
        showError(s);
    }

    private void showError(String error){
        if(mCB != null){
            if(error != null){
                if(error.contains("UserRecoverableAuthIOException")){
                    UserRecoverableAuthIOException e = (UserRecoverableAuthIOException) mLastError;
                    mCB.onUserRecoverableAuthIOException(e);
                }
                else{
                    mCB.onError("AppConfigRequest onCancelled." + error);
                }
            }
            else{
                mCB.onError("AppConfigRequest onCancelled.");
            }
        }
    }
}
