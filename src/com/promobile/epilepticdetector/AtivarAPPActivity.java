package com.promobile.epilepticdetector;

import android.app.Activity;
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
	
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	Button start, stop;
	int ID = 101;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ativar_app);
		   //botões de ativar e desativar o app
  		start=(Button)findViewById(R.id.btnBackgroundOn); //botão ligar
          stop=(Button)findViewById(R.id.btnBackgroundOff); //botão desligar
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
    Intent intent=new Intent(getApplicationContext(), AcelerometroActivity_.class);
    // Try to stop the service if it is already running
    intent.addCategory(AcelerometroActivity_.TAG);
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
	Intent intent=new Intent(getApplicationContext(), AcelerometroActivity_.class);
	intent.addCategory(AcelerometroActivity_.TAG);
	stopService(intent);
	mNotifyManager =
	        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	mNotifyManager.cancel(ID);
	
}

public void showNotification(){
	mBuilder = new NotificationCompat.Builder(this);
	mBuilder.setContentTitle("Epileptic Detector")
	    .setContentText("Monitorando possível ataque")
	    .setSmallIcon(R.drawable.hd);
	Intent resultIntent = new Intent(this, MainActivity.class);
	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	stackBuilder.addParentStack(MainActivity.class);
	stackBuilder.addNextIntent(resultIntent);
	PendingIntent resultPendingIntent =
	        stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_UPDATE_CURRENT
	        );
	mBuilder.setContentIntent(resultPendingIntent);
	mNotifyManager =
	        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	mNotifyManager.notify(ID, mBuilder.build());	
}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ativar_ap, menu);
		return true;
	}

}
