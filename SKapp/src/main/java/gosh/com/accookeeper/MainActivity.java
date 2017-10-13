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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import gosh.com.accookeepersdk.AccookeeperSDK;
import gosh.com.accookeepersdk.activity.SettingsActivity;
import gosh.com.accookeepersdk.googledrive.GoogleDriveAPI;
import gosh.com.accookeepersdk.request.AppConfigRequest;
import gosh.com.accookeepersdk.request.RequestCallback;


public class MainActivity extends AppCompatActivity{
    private final static String TAG = MainActivity.class.getName();
    private final static int REQUEST_CODE_CAPTURE_IMAGE = 12;

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if(id == R.id.action_sheet){
            //startActivity(new Intent(this, SheetActivity.class));
            AppConfigRequest request = new AppConfigRequest(AccookeeperSDK.getInstance().getGoogleAccountCredential(this), this, new RequestCallback() {
                @Override
                public void onFinishedFetchData(String data) {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                    if(f instanceof  MainActivityFragment){
                        MainActivityFragment mf = (MainActivityFragment) f;
                        mf.setMessage(data);
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
            request.execute();

//            MakeRequestTask task = new MakeRequestTask(AccookeeperSDK.getInstance().getGoogleAccountCredential(this), this, new MakeRequestTask.ABC() {
//                @Override
//                public void onFinish(String msg) {
//
//                }
//            });
//            task.execute();
            return true;
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
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
