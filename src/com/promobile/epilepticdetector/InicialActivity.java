package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RadioButton;

public class InicialActivity extends Activity {
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	RadioButton start, stop;
	int ID = 101;

	private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
	private final int TIMER_MONITORAMENTO_HEURISTICA = 30; // EM milisegundos...

	 @Override
     public void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_inicial);
         
         alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
         
         start = (RadioButton) findViewById(R.id.btnBackgroundOn); //botao ligar
         stop = (RadioButton) findViewById(R.id.btnBackgroundOff); //botao desligar
         start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
            	 	start.setChecked(true);
            	 	stop.setChecked(false);
          	 		startSensing();
             	 }
             });
         
         
         stop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) { 
         	 	start.setChecked(false);
         	 	stop.setChecked(true);
             	stopSensing();}
             });        
         
         //TODO: NAO REMOVER O BOTAO, POIS PARA DEBUGAR A HEURISTICA DE DESMAIO EH MAIS FACIL DEVIDO OS LOGS!!!!
         findViewById(R.id.btnAutomatedTestHeuristic).setOnClickListener(new View.OnClickListener() {
    		@Override
        		public void onClick(View arg0) {
        			Intent intent = new Intent(InicialActivity.this, AutomatedTestHeuristicActivity.class);
        			startActivity(intent);
        		}
        	});
	}
	
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