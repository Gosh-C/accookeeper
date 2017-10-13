package gosh.com.accookeepersdk.request;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gosh.com.accookeepersdk.utils.PrefUtils;

/**
 * Created by goshchan on 13/10/2017.
 */

public class AppendRequest extends RequestBase {
    private Context mContext;
    private RequestCallback mCB;

    public AppendRequest(GoogleAccountCredential credential, Context context, RequestCallback cb) {
        super(credential);
        mContext = context;
        mCB = cb;
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
            String spreadSheetId = PrefUtils.getAppConfigResourceId(mContext);
            List obj = new ArrayList<Object>();
            obj.add("A");
            obj.add("B");
            obj.add(new Date().toString());
            obj.add(Boolean.FALSE.toString());
            String updatedRange = appendRow(spreadSheetId, "TESTING", obj);
            return updatedRange;
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
                    mCB.onError("AppendRequest onCancelled." + error);
                }
            }
            else{
                mCB.onError("AppendRequest onCancelled.");
            }
        }
    }
}
