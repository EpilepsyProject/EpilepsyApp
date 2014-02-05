package com.promobile.epilepticdetector;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class TipoPerfil extends Activity {

	public static final String PREFS_NAME = "Preferences";
	NotificationCompat.Builder mBuilder;
	NotificationManager mNotifyManager;
	String TAG = "MainActivity";
	RadioButton start, stop;
	int ID = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tipo_perfil);
	
		 final ImageButton moderado = (ImageButton) findViewById(R.id.imageButton1);
		 final ImageButton preciso = (ImageButton) findViewById(R.id.imageButton2);      
	
	     SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	     final boolean optionM = settings.getBoolean("perfilModerado", true); 
	     final boolean optionP = settings.getBoolean("perfilPreciso", false);
	     
	     moderado.setOnClickListener(new OnClickListener() {
        	 
				@Override
				public void onClick(View arg0) {
	     			
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
					SharedPreferences.Editor editor = settings.edit(); 
					editor.putBoolean("perfilModerado", optionM); 
					editor.commit();// Commit the edits! editor.

           	 		moderado.setPressed(true);
           	 		preciso.setPressed(false);
           	 		startSensingModerado(); 
				  
           	 		Toast.makeText(getApplicationContext(), "EpilepsyApp - Perfil Moderadao selecionado!" , Toast.LENGTH_SHORT).show();
				}
	 
			});
	     
	     preciso.setOnClickListener(new OnClickListener() {
        	 
				@Override
				public void onClick(View arg0) {
	     			
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
					SharedPreferences.Editor editor = settings.edit(); 
					editor.putBoolean("perfilPreciso", optionP); 
					editor.commit();// Commit the edits! editor.
							
					moderado.setPressed(true);
           	 		preciso.setPressed(false);
           	 		startSensingPreciso(); 
					Toast.makeText(getApplicationContext(), "EpilepsyApp - Perfil Preciso selecionado!" , Toast.LENGTH_SHORT).show();
				}
	 
			});
	     
	}
		
/******************************	SERVICE DO PERFIL MODERADO	******************************/
	
		private void toggleServiceModerado(){
		   Intent intent=new Intent(getApplicationContext(), EpilepsyHeuristicModerado.class);
		   // Try to stop the service if it is already running
		   intent.addCategory(EpilepsyHeuristicService.TAG);
		   if(!stopService(intent)){
		       startService(intent);
		   }
		}
		
		public void startSensingModerado(){
			showNotification();
			toggleServiceModerado();
			//this.finish();
		}
		
		private void stopSensingModerado(){
			Intent intent = new Intent(getApplicationContext(), EpilepsyHeuristicModerado.class);
			intent.addCategory(EpilepsyHeuristicService.TAG);
			stopService(intent);
			mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyManager.cancel(ID);
			
		}
				
		/******************************	SERVICE DO PERFIL PRECISO	******************************/
		
		private void toggleServicePreciso(){
			   Intent intent=new Intent(getApplicationContext(), EpilepsyHeuristicPrecisao.class);
			   // Try to stop the service if it is already running
			   intent.addCategory(EpilepsyHeuristicService.TAG);
			   if(!stopService(intent)){
			       startService(intent);
			   }
			}
			
			public void startSensingPreciso(){
				showNotification();
				toggleServicePreciso();
				//this.finish();
			}
			
			private void stopSensingPreciso(){
				Intent intent = new Intent(getApplicationContext(), EpilepsyHeuristicPrecisao.class);
				intent.addCategory(EpilepsyHeuristicService.TAG);
				stopService(intent);
				mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotifyManager.cancel(ID);
				
			}
			
			
			/******************************	BARRA DE NOTIFICAÇÃO ******************************/
		
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
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyManager.notify(ID, mBuilder.build());	
		         
		     }

}
