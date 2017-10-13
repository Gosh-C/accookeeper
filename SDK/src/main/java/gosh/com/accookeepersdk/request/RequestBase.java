package gosh.com.accookeepersdk.request;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.util.List;

/**
 * Created by goshchan on 12/10/2017.
 */

public abstract class RequestBase extends AsyncTask<Void, Void, List<String>>{
    protected Sheets mService;
    protected Exception mLastError = null;

    RequestBase(GoogleAccountCredential credential){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Sheets.Builder(transport, jsonFactory, credential).build();
    }
}
