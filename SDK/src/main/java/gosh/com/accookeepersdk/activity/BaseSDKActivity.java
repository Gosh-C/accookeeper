package gosh.com.accookeepersdk.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import gosh.com.accookeepersdk.AccookeeperSDK;
import gosh.com.accookeepersdk.AppConstants;
import gosh.com.accookeepersdk.R;
import gosh.com.accookeepersdk.googledrive.GoogleDriveAPI;
import gosh.com.accookeepersdk.googledrive.GoogleDriveConnectionCallback;
import gosh.com.accookeepersdk.utils.PrefUtils;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by goshchan on 10/10/2017.
 */

public abstract class BaseSDKActivity extends AppCompatActivity implements GoogleDriveConnectionCallback {
    private final static String TAG = BaseSDKActivity.class.getName();

    private final static int REQUEST_CODE_RESOLUTION = 1;
    private final static int REQUEST_ACCOUNT_PICKER = 1000;
    private final static int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private final static int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private boolean mFinishedConfig = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleDriveAPI.getInstance().init(this);
        init();
    }

    private void init(){
        AppConstants.STATUS status = AccookeeperSDK.getInstance().init(this);
        switch(status){
            case GOOGLE_PLAY_SERVICE_NOT_AVAILABLE:
                acquireGooglePlayServices();
                break;
            case SELECT_ACCOUNT:
                chooseAccount();
                break;
            case DEVICE_OFFLINE:
                Toast.makeText(this, "Device is offline", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                //Toast.makeText(this, "Init SDK Success", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleDriveAPI.onResume();
        GoogleDriveAPI.getInstance().setConnectionCallbackListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleDriveAPI.onPause();
        GoogleDriveAPI.getInstance().setConnectionCallbackListener(null);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    GoogleDriveAPI.getInstance().connect();
                }
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                }
                else{
                    init();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        PrefUtils.putCredentialAccountName(this, accountName);
                        AccookeeperSDK.getInstance().setCredentialAccountName(this, accountName);
                        init();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected() {
        if(!mFinishedConfig){
            if("".equalsIgnoreCase(PrefUtils.getRootFolderID(this))
                    || "".equalsIgnoreCase(PrefUtils.getAppConfigResourceId(this))){
                Toast.makeText(this, getString(R.string.finish_app_config), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
            else{
                mFinishedConfig = true;
                onFinishedConfiguration();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this, "onConnectionSuspended: " + i, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                BaseSDKActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = PrefUtils.getCredentialAccountName(this);
            if (accountName != null) {
                AccookeeperSDK.getInstance().setCredentialAccountName(this, accountName);
                AccookeeperSDK.getInstance().init(this);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(AccookeeperSDK.getInstance().getGoogleAccountCredential(this).newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        chooseAccount();
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    protected boolean isFinishedConfig(){
        return mFinishedConfig;
    }

    protected abstract void onFinishedConfiguration();
}
