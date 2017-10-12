package gosh.com.accookeepersdk;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gosh.com.accookeepersdk.utils.PrefUtils;

/**
 * Created by goshchan on 11/10/2017.
 */

public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private Sheets mService;
    private Exception mLastError = null;
    private Context mContext;
    private ABC mAbc;

    public MakeRequestTask(GoogleAccountCredential credential, Context context, ABC abc) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Sheets.Builder(transport, jsonFactory, credential).build();
        mContext = context;
        mAbc = abc;
    }

    /**
     * Fetch a list of names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     * @return List of names and majors
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        String spreadsheetId = PrefUtils.getAppConfigResourceId(mContext);
        String range = "Config!A1:E";
        List<String> results = new ArrayList<String>();
        ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values != null) {
            for (List row : values) {
                results.add(row.get(0) + ", " + row.get(1)+ ", " + row.get(2)+ ", " + row.get(3) + ", " + row.get(4));
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        if (output == null || output.size() == 0) {
            mAbc.onFinish("No results returned.");
        } else {
            mAbc.onFinish(TextUtils.join("\n", output));
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

    public static interface ABC{
        void onFinish(String msg);
    }
}
