package com.promobile.epilepticdetector;

import com.promobile.epilepticdetector.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

@SuppressLint("NewApi")
public class InicialActivity extends Activity {
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

	 @SuppressLint("NewApi")
	@Override
     public void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_inicial);
         
         ActionBar actionBar = getActionBar();
	        actionBar.setDisplayHomeAsUpEnabled(true);
         
         alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
             
         
         //TODO: NAO REMOVER O BOTAO, POIS PARA DEBUGAR A HEURISTICA DE DESMAIO EH MAIS FACIL DEVIDO OS LOGS!!!!
         findViewById(R.id.btnAutomatedTestHeuristic).setOnClickListener(new View.OnClickListener() {
    		@Override
        		public void onClick(View arg0) {
    				Toast.makeText(getApplicationContext(), "EpilepsyApp - Testes Automatizados!", Toast.LENGTH_SHORT).show();
        			Intent intent = new Intent(InicialActivity.this, AutomatedTestHeuristicActivity.class);
        			startActivity(intent);
        		}
        	});
         
         // Teste envio de SMS... :-) OBS.: EH NECESSARIO TER DOIS EMULADORES ABERTOS!!!!!!
         findViewById(R.id.btnSmsSendTest).setOnClickListener(new View.OnClickListener() {
    		@Override
        		public void onClick(View arg0) {
    				contSms = contSms + 1;
    			
    				String phoneNumber = "5556";
    				String mensagem = "Teste envio SMS numero " + Integer.toString(contSms);
	    			
    				SmsManager sms = SmsManager.getDefault();
	    			sms.sendTextMessage(phoneNumber, null, mensagem, null, null);
	    			
	    			Toast.makeText(getApplicationContext(), "EpilepsyApp - Mensagem Enviada!", Toast.LENGTH_SHORT).show();
        		}
        	});
         
         // Testando GPS
         findViewById(R.id.btnTestGps).setOnClickListener(new View.OnClickListener() {
    		@Override
        		public void onClick(View arg) {
    				Toast.makeText(getApplicationContext(), "EpilepsyApp - GPS e Google maps!", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(InicialActivity.this, GpsTestActivity.class);
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
	
	public void showDialogClick(View view){
		Intent intent = new Intent(this, TelaDesmaio.class);
		this.startActivity(intent);
	}
	
}