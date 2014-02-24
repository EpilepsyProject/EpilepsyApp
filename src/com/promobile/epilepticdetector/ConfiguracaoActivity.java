package com.promobile.epilepticdetector;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RadioButton;



@SuppressLint("NewApi")
public class ConfiguracaoActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	RadioButton start, stop;
	int ID = 101;
	
	int contSms = 0;

	private PendingIntent pendingIntent;
    @SuppressWarnings("unused")
	private AlarmManager alarmManager;
	private final int TIMER_MONITORAMENTO_HEURISTICA = 30; // EM milisegundos...
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
				// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);
		
			
		
		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("Notificação");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);
		

/******** CONTATOS ESTÁ COMENTADO POIS AO INICIAR A APLICAÇÃO ELE A TRAVA! CONSERTAR POSTERIORMENTE ********/
		
		// Add 'data and sync' preferences, and a corresponding header.
		/*fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("Contatos");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_contatos);*/

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		if (key.equals("pref_key_inicio")) {
			startSensing();	
		}
		
		else if(key.equals("pref_key_inicio")){
				stopSensing();

			
		}
           // Toast.makeText(this, "Serviço Iniciado", Toast.LENGTH_SHORT).show();
      if (key.equals("pref_key_ringtore")){
        	stopSensing();
        	
        }
        	
        }
	

	
/************************************ SERVIÇO ************************************/	
	
	private void toggleService(){
		Context context = getApplicationContext();
		Intent intent = new Intent(context, EpilepsyHeuristicService.class);
		// Try to stop the service if it is already running
		intent.addCategory(EpilepsyHeuristicService.TAG);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TIMER_MONITORAMENTO_HEURISTICA, pendingIntent);
   
		if(!stopService(intent)){
			startService(intent);
		}
	}
	
	public void startSensing(){
		showNotification();
		toggleService();
		//this.finish();
	}
	
	private void stopSensing(){
		Intent intent = new Intent(getApplicationContext(), EpilepsyHeuristicService.class);
		intent.addCategory(EpilepsyHeuristicService.TAG);
		stopService(intent);
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyManager.cancel(ID);
		
		Context context = getApplicationContext();
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
	
	public void showNotification(){
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("Epileptic Detector")
		    .setContentText("Monitorando possível ataque")
		    .setSmallIcon(R.drawable.ic_launcher);
		Intent resultIntent = new Intent(this, MainActivity.class);
	
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_ONE_SHOT
		        );
		mBuilder.setOngoing(true);
		mBuilder.setContentIntent(resultPendingIntent);
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyManager.notify(ID, mBuilder.build());	
		
     }
}
