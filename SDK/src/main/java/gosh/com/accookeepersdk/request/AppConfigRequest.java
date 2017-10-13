package gosh.com.accookeepersdk.request;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import gosh.com.accookeepersdk.AppConfig;
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
    private String getDataFromApi() throws IOException, JSONException {
        String spreadSheetId = PrefUtils.getAppConfigResourceId(mContext);
        List<String> titles = getAllSheetsTitle(spreadSheetId);
        HashMap<String, LinkedList<LinkedHashMap<String, String>>> sheetsConfig = new HashMap<>();

//        StringBuilder sb = new StringBuilder();
        if(titles != null && titles.size() > 0){
            for(String title : titles){
//                sb.append(title).append("\n");
                List<List<Object>> values = getDataInSheet(spreadSheetId, title);
                if(values != null && values.size() > 0){
                    LinkedList l = new LinkedList();
                    for (List row : values) {
//                        sb.append("\t").append(row.get(0));
//                        sb.append("\t").append(row.get(1));
//                        sb.append("\t").append(row.get(2));
//                        sb.append("\n");

                        LinkedHashMap<String, String> field = new LinkedHashMap<>();
                        field.put("FIELD", row.get(0)+"");
                        field.put("TYPE", row.get(1)+"");
                        field.put("MANDATORY", row.get(2)+"");
                        l.add(field);
                    }
                    sheetsConfig.put(title, l);
                }
            }
        }
        return AppConfig.toJsonString(sheetsConfig);
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
