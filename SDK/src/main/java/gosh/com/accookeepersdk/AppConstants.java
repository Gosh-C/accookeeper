package gosh.com.accookeepersdk;

/**
 * Created by goshchan on 9/10/2017.
 */

public class AppConstants {
    public final static String DRIVE_PREF = "GoogleDrivePref";
    public final static String PREF_ROOT_FOLER = "root_folder";
    public final static String PREF_CRED_ACC_NAME = "credential_account_name";
    public final static String PREF_APP_CONFIG_RES_ID = "app_config_resource_id";

    public enum STATUS{
        GOOGLE_PLAY_SERVICE_NOT_AVAILABLE,
        SELECT_ACCOUNT,
        DEVICE_OFFLINE,
        SUCCESS
    }
}
