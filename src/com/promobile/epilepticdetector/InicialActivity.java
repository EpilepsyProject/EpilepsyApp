package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class InicialActivity extends Activity {
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	Button start, stop;
	int ID = 101;
	
	 @Override
     public void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_inicial);
         
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
   Intent intent=new Intent(getApplicationContext(), AcelerometroActivity.class);
   // Try to stop the service if it is already running
   intent.addCategory(AcelerometroActivity.TAG);
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
	Intent intent=new Intent(getApplicationContext(), AcelerometroActivity.class);
	intent.addCategory(AcelerometroActivity.TAG);
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

}
