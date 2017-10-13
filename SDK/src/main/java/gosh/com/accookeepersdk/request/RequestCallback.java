package gosh.com.accookeepersdk.request;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

/**
 * Created by goshchan on 12/10/2017.
 */

public interface RequestCallback {
    void onFinishedFetchData(String data);
    void onError(String error);
    void onUserRecoverableAuthIOException(UserRecoverableAuthIOException e);
}
