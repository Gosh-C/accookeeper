package gosh.com.accookeepersdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static gosh.com.accookeepersdk.AppConstants.DRIVE_PREF;
import static gosh.com.accookeepersdk.AppConstants.PREF_APP_CONFIG_RES_ID;
import static gosh.com.accookeepersdk.AppConstants.PREF_CRED_ACC_NAME;
import static gosh.com.accookeepersdk.AppConstants.PREF_ROOT_FOLER;

/**
 * Created by goshchan on 9/10/2017.
 */

public class PrefUtils {
    private final static String TAG = PrefUtils.class.getName();

    public static void putRootFolderID(Context context, String rootDriveId){
        putString(context, PREF_ROOT_FOLER, rootDriveId);
    }

    public static String getRootFolderID(Context context){
        return getString(context, PREF_ROOT_FOLER, "");
    }

    public static void putCredentialAccountName(Context context, String accountName){
        putString(context, PREF_CRED_ACC_NAME, accountName);
    }

    public static String getCredentialAccountName(Context context){
        return getString(context, PREF_CRED_ACC_NAME, null);
    }

    public static void putAppConfigResourceId(Context context, String resourceId){
        putString(context, PREF_APP_CONFIG_RES_ID, resourceId);
    }

    public static String getAppConfigResourceId(Context context){
        return getString(context, PREF_APP_CONFIG_RES_ID, "");
    }



    private static void putString(Context context, String key, String value){
        try{
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences(DRIVE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, value);
            editor.commit();
        }
        catch(Exception e){
            Log.e(TAG, "Put Root Folder Id Error");
        }
    }

    private static String getString(Context context, String key, String defValue){
        try{
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences(DRIVE_PREF, Context.MODE_PRIVATE);
            return pref.getString(key, defValue);
        }
        catch(Exception e){
            return defValue;
        }
    }
}
