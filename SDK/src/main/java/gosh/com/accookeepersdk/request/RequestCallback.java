package gosh.com.accookeepersdk.request;

/**
 * Created by goshchan on 12/10/2017.
 */

public interface RequestCallback {
    void onFinishedFetchData(String data);
    void onError(String error);
}
