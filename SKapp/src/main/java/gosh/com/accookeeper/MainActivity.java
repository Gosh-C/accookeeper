package gosh.com.accookeeper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.Lists;

import java.util.ArrayList;
import java.util.List;

import gosh.com.accookeepersdk.AccookeeperSDK;
import gosh.com.accookeepersdk.SheetsConfig;
import gosh.com.accookeepersdk.activity.SettingsActivity;
import gosh.com.accookeepersdk.googledrive.GoogleDriveAPI;
import gosh.com.accookeepersdk.model.SheetField;
import gosh.com.accookeepersdk.request.AppendRequest;
import gosh.com.accookeepersdk.request.RequestCallback;
import gosh.com.accookeepersdk.utils.PrefUtils;


public class MainActivity extends AppCompatActivity{
    private final static String TAG = MainActivity.class.getName();
    private final static int REQUEST_CODE_CAPTURE_IMAGE = 1001;
    private final static int REQUEST_CODE_AUTHORIZATION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_testing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_sheet:
                String data = PrefUtils.getSheetConfigJson(this);
                showMsg(data);
                return true;
            case R.id.action_append:
                AppendRequest apRequest = new AppendRequest(AccookeeperSDK.getInstance().getGoogleAccountCredential(this), this, new RequestCallback() {
                    @Override
                    public void onFinishedFetchData(String data) {
                        showMsg(data);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserRecoverableAuthIOException(UserRecoverableAuthIOException e) {
                        startActivityForResult(e.getIntent(), REQUEST_CODE_AUTHORIZATION);
                    }
                });
                apRequest.execute();
                break;
            case R.id.action_get_sheet_name:
                try {
                    showMsg(TextUtils.join("\n", SheetsConfig.getSheetName(this)));
                }
                catch(Exception e){
                    SheetsConfig.resetSheet();
                    Toast.makeText(this, "Invalid Sheet Config File.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_get_sheet_field:
                try {
                    List<SheetField> fields =  SheetsConfig.getSheetFields(this, "TESTING");
                    ArrayList<String> s = Lists.newArrayList();
                    for(SheetField sf : fields){
                        s.add(sf.getFieldName());
                    }
                    showMsg(TextUtils.join("\n", s));
                }
                catch(Exception e){
                    SheetsConfig.resetSheet();
                    Toast.makeText(this, "Invalid Sheet Config File.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmapToSave = (Bitmap) data.getExtras().get("data");
                    GoogleDriveAPI.getInstance().uploadImageToDrive(this, bitmapToSave);
                }
                break;
            case REQUEST_CODE_AUTHORIZATION:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showMsg(String msg){
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if(f instanceof  MainActivityFragment){
            MainActivityFragment mf = (MainActivityFragment) f;
            mf.setMessage(msg);
        }
    }
}
