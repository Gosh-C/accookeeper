package gosh.com.accookeepersdk.googledrive;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import gosh.com.accookeepersdk.R;
import gosh.com.accookeepersdk.utils.PrefUtils;

import static com.google.android.gms.drive.DriveId.decodeFromString;

/**
 * Created by goshchan on 9/10/2017.
 */

public class GoogleDriveAPI {
    private final static String TAG = GoogleDriveAPI.class.getName();

    private static GoogleDriveAPI sInstant;
    private static GoogleApiClient sGoogleApiClient;
    private GoogleDriveConnectionCallback mConnectionCallback;

    public static GoogleDriveAPI getInstance(){
        if(sInstant == null){
            sInstant = new GoogleDriveAPI();
        }
        return sInstant;
    }

    public void setConnectionCallbackListener(GoogleDriveConnectionCallback cb){
        mConnectionCallback = cb;
    }

    public static void onResume(){
        if (sGoogleApiClient != null) {
            sGoogleApiClient.connect();
        }
    }

    public static void onPause(){
        if (sGoogleApiClient != null) {
            sGoogleApiClient.disconnect();
        }
    }

    public void connect(){
        onResume();
    }

    public void init(final Context context){
        if (sGoogleApiClient == null) {
            sGoogleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if(mConnectionCallback != null){
                                mConnectionCallback.onConnected();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            if(mConnectionCallback != null){
                                mConnectionCallback.onConnectionSuspended(i);
                            }
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            if(mConnectionCallback != null){
                                mConnectionCallback.onConnectionFailed(connectionResult);
                            }
                        }
                    })
                    .build();
        }
    }

    public void uploadImageToDrive(Context c, Bitmap bitmap){
        uploadImageToDrive(c, bitmap, c.getString(R.string.app_name));
    }

    public void uploadImageToDrive(Context c, Bitmap bitmap, final String folderName) {
        final Context context = c.getApplicationContext();
        Log.i(TAG, "saveFileToDrive() Creating new content.");
        final Bitmap image = bitmap;
        Drive.DriveApi.newDriveContents(sGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed to create new content!.");
                    return;
                }
                Log.i(TAG, "New content has been created.");
                OutputStream outputStream = result.getDriveContents().getOutputStream();
                // Write the bitmap data from it.
                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                try {
                    outputStream.write(bitmapStream.toByteArray());
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }

//                // Create the initial metadata - MIME type and title.
//                // Note that the user will be able to change the title later.
                final MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg").setTitle("myPhoto.png").build();

                //DriveID: xxxx
                String id = PrefUtils.getRootFolderID(context);
                uploadWithDriveID(context, id, metadataChangeSet, result);
            }
        });
    }

    private void uploadWithDriveID(final Context context, String id, MetadataChangeSet metadataChangeSet, DriveApi.DriveContentsResult result){
        DriveId did = decodeFromString(id);
        DriveFolder folder = did.asDriveFolder();
        folder.createFile(sGoogleApiClient, metadataChangeSet, result.getDriveContents())
                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Toast.makeText(context, "Error while trying to create the file", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(context, "Created a file: " + result.getDriveFile().getDriveId(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadWithFolderName(final Context context, String resId, final MetadataChangeSet metadataChangeSet, final DriveApi.DriveContentsResult result, final String folderName) {
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, folderName), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(sGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()){
                    Toast.makeText(context, "Cannot found folder:" + folderName, Toast.LENGTH_SHORT).show();
                }
                else{
                    DriveId did = null;
                    for (Metadata m : metadataBufferResult.getMetadataBuffer()){
                        if (m.getTitle().equals(folderName)) {
                            Log.e(TAG, "Folder exists");
                            did = m.getDriveId();
                            //break;
                        }
                    }

                    if(did != null){
                        DriveFolder folder = did.asDriveFolder();
                        folder.createFile(sGoogleApiClient, metadataChangeSet, result.getDriveContents()).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override
                            public void onResult(DriveFolder.DriveFileResult r) {
                                if (!r.getStatus().isSuccess()) {
                                    Toast.makeText(context, "Error while trying to create the file", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(context, "Created a file: " + r.getDriveFile().getDriveId(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }


    private void getDriveIdFromFolderName(final String folderName){
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, folderName), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(sGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()){
                    Log.e(TAG, "Cannot create folder in the root.");
                }
                else{
                    DriveId did;
                    for (Metadata m : metadataBufferResult.getMetadataBuffer()){
                        if (m.getTitle().equals(folderName)) {
                            Log.e(TAG, "Folder exists");
                            did = m.getDriveId();
                            break;
                        }
                    }
                }
            }
        });
    }

    public void createFolder(final Context context, String folderName){
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(folderName).build();

        Drive.DriveApi.getRootFolder(sGoogleApiClient).createFolder(
                sGoogleApiClient, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult result) {
                if (!result.getStatus().isSuccess()) {
                    Toast.makeText(context, "Error while trying to create the folder", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Created a folder: " + result.getDriveFolder().getDriveId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetch(){
        Query query = new Query.Builder().build();
        Drive.DriveApi.getRootFolder(sGoogleApiClient).queryChildren(sGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.d(TAG, "Problem while retrieving files");
                    return;
                }
                MetadataBuffer buffer = result.getMetadataBuffer();
                int count = buffer.getCount();
                Log.d(TAG, ""+count);
            }
        });
    }

    public void folderPicker(Activity activity, int requestCode){
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { DriveFolder.MIME_TYPE })
                .build(sGoogleApiClient);
        try {
            activity.startIntentSenderForResult(
                    intentSender, requestCode, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }

    public void filePicker(Activity activity, int requestCode){
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] {"application/vnd.google-apps.spreadsheet"})
                .build(sGoogleApiClient);
        try {
            activity.startIntentSenderForResult(
                    intentSender, requestCode, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }
}
