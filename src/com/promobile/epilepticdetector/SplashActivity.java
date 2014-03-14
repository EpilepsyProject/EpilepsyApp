package com.promobile.epilepticdetector;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.promobile.epilepticdetector.R;
 
public class SplashActivity extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        PhoneCallListener phoneListener = new PhoneCallListener();
        phoneListener.phoneStateListener(this);
		TelephonyManager telephonyManager = (TelephonyManager) this
			.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
		GPSTracker gpsTracker = new GPSTracker(this);
		if(!gpsTracker.canGetLocation()){
			gpsTracker.showSettingsAlert();
		}
		
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	 Intent intent = new Intent();
                 intent.setClass(SplashActivity.this, MainActivity.class);
                 startActivity(intent);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}
