package gosh.com.accookeepersdk.request;

import android.content.Context;
import android.text.TextUtils;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
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
    private List<String> getDataFromApi() throws IOException {
        String spreadsheetId = PrefUtils.getAppConfigResourceId(mContext);
        String range = "Config";
        List<String> results = new ArrayList<>();
        ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values != null) {
            for (List row : values) {
                results.add(row.get(0) + "" );
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        if (output == null || output.size() == 0) {
            if(mLastError != null){
                if(mCB != null){
                    mCB.onError("Invalid Config File:\n" + mLastError.toString());
                }
            }
            else{
                if(mCB != null){
                    mCB.onError("Invalid Congif File.");
                }
            }
        } else {
            mCB.onFinishedFetchData(TextUtils.join("\n", output));
        }
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }
}
