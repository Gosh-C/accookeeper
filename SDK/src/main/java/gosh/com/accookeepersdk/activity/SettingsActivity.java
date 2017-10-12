package gosh.com.accookeepersdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import gosh.com.accookeepersdk.utils.PrefUtils;
import gosh.com.accookeepersdk.R;
import gosh.com.accookeepersdk.googledrive.GoogleDriveAPI;

/**
 * Created by goshchan on 10/10/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ROOT_FOLDER = 1;
    private final static int REQUEST_CODE_CONFIG_FILE = 2;

    private EditText mGoogleDriveRootFolder, mConfigFile;
    private Button mSelectRoot, mSelectConfig;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        mGoogleDriveRootFolder = (EditText)findViewById(R.id.drive_root_folder);
        mConfigFile = (EditText) findViewById(R.id.app_config_file);
        mSelectRoot = (Button)findViewById(R.id.select_root);
        mSelectConfig = (Button)findViewById(R.id.select_config);


        mSelectRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleDriveAPI.getInstance().folderPicker(SettingsActivity.this, REQUEST_CODE_ROOT_FOLDER);
            }
        });

        mSelectConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleDriveAPI.getInstance().filePicker(SettingsActivity.this, REQUEST_CODE_CONFIG_FILE);
            }
        });
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleDriveAPI.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleDriveAPI.onPause();
    }

    private void init(){
        String root = PrefUtils.getRootFolderID(this);
        mGoogleDriveRootFolder.setText(root);
        String config = PrefUtils.getAppConfigResourceId(this);
        mConfigFile.setText(config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_ROOT_FOLDER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    String selectedFolder = driveId.encodeToString();
                    mGoogleDriveRootFolder.setText(selectedFolder);
                    PrefUtils.putRootFolderID(this, selectedFolder);
                }
                break;
            case REQUEST_CODE_CONFIG_FILE:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    String selectedFile = driveId.getResourceId();
                    mConfigFile.setText(selectedFile);
                    PrefUtils.putAppConfigResourceId(this, selectedFile);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
