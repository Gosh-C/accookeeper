package gosh.com.accookeeper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import gosh.com.accookeepersdk.activity.BaseSDKActivity;

/**
 * Created by goshchan on 12/10/2017.
 */

public class SplashActivity extends BaseSDKActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onFinishedConfiguration(){
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }
}
