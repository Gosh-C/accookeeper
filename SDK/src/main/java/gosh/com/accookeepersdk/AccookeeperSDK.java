package gosh.com.accookeepersdk;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Arrays;

import gosh.com.accookeepersdk.utils.SystemUtils;

import static gosh.com.accookeepersdk.AppConstants.STATUS.DEVICE_OFFLINE;
import static gosh.com.accookeepersdk.AppConstants.STATUS.GOOGLE_PLAY_SERVICE_NOT_AVAILABLE;
import static gosh.com.accookeepersdk.AppConstants.STATUS.SELECT_ACCOUNT;
import static gosh.com.accookeepersdk.AppConstants.STATUS.SUCCESS;

/**
 * Created by goshchan on 9/10/2017.
 */

public class AccookeeperSDK {
    private final static String TAG = AccookeeperSDK.class.getName();
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    private GoogleAccountCredential mCredential;

    private static AccookeeperSDK sInstance;


    public static AccookeeperSDK getInstance(){
        if(sInstance == null){
            sInstance = new AccookeeperSDK();
        }
        return sInstance;
    }

    private void initCredential(Context context){
        if(mCredential == null){
            mCredential = GoogleAccountCredential.usingOAuth2(
                    context.getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
        }

    }

    public void setCredentialAccountName(Context context, String name){
        initCredential(context);
        mCredential.setSelectedAccountName(name);
    }

    public GoogleAccountCredential getGoogleAccountCredential(Context context){
        initCredential(context);
        return mCredential;
    }

    public AppConstants.STATUS init(Context context){
        initCredential(context);
        if (!isGooglePlayServicesAvailable(context)) {
            return GOOGLE_PLAY_SERVICE_NOT_AVAILABLE;
        } else if (mCredential.getSelectedAccountName() == null) {
            return SELECT_ACCOUNT;
        } else if (!SystemUtils.isDeviceOnline(context)) {
            return DEVICE_OFFLINE;
        }
        return SUCCESS;
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
}
