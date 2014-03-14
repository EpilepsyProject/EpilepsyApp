package com.promobile.epilepticdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

//monitor phone call activities
public class PhoneCallListener extends PhoneStateListener {

	private boolean isPhoneCalling = false;
	private Activity context;
	String LOG_TAG = "LOGGING 123";
	
	public void phoneStateListener(Activity context) {
		this.context = context;
	}
	

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		if (TelephonyManager.CALL_STATE_RINGING == state) {
			// phone ringing
			Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
		}

		if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
			// active
			Log.i(LOG_TAG, "OFFHOOK");

			isPhoneCalling = true;
		}

		if (TelephonyManager.CALL_STATE_IDLE == state) {
			// run when class initial and phone call ended, 
			// need detect flag from CALL_STATE_OFFHOOK
			Log.i(LOG_TAG, "IDLE");

			if (isPhoneCalling) {

				Log.i(LOG_TAG, "restart app");

				// restart app
				Intent i = context.getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(context.getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				context.startActivity(i);

				isPhoneCalling = false;
			}

		}
	}
}