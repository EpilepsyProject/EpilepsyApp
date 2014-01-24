package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class AtivarAPPActivity extends Activity {
	
	Notification notification;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	Button start, stop;
	int NOTIFY_ID = 101;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ativar_app);
		   //botoes de ativar e desativar o app
  		start=(Button)findViewById(R.id.btnBackgroundOn); //botao ligar
          stop=(Button)findViewById(R.id.btnBackgroundOff); //botao desligar
          start.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {                
              	startSensing();
              	}
              });
          
          
          stop.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) { 
              	stopSensing();}
              });        
	}
	
	private void toggleService(){
	    Intent intent=new Intent(getApplicationContext(), EpilepsyHeuristicService.class);
	    // Try to stop the service if it is already running
	    intent.addCategory(EpilepsyHeuristicService.TAG);
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
		Intent intent=new Intent(getApplicationContext(), EpilepsyHeuristicService.class);
		intent.addCategory(EpilepsyHeuristicService.TAG);
		stopService(intent);
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyManager.cancel(NOTIFY_ID);
		
	}
	
	public void showNotification(){
		Context context = getApplicationContext();
		Intent intent = new Intent(this, MainActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		notification = new Notification();
		notification.icon = R.drawable.hd;
		notification.tickerText = "Monitorando possível ataque";
		notification.when = System.currentTimeMillis();

		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
		notification.contentIntent = pendingIntent;
		
		mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyManager.notify(NOTIFY_ID, notification);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ativar_ap, menu);
		return true;
	}

}
