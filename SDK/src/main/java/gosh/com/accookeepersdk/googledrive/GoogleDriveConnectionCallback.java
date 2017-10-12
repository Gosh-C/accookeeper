package gosh.com.accookeepersdk.googledrive;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by goshchan on 9/10/2017.
 */

public interface GoogleDriveConnectionCallback {
    void onConnected();
    void onConnectionSuspended(int i);
    void onConnectionFailed(ConnectionResult result);
}
