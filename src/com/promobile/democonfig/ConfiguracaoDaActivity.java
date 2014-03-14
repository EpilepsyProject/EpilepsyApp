package com.promobile.democonfig;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v4.app.NotificationCompat;
import android.widget.RadioButton;
import android.widget.Toast;
import com.promobile.contatos.Contato;
import com.promobile.contatos.ContatosUtil;
import com.promobile.epilepticdetector.EpilepsyHeuristic;
import com.promobile.epilepticdetector.EpilepsyHeuristicService;
import com.promobile.epilepticdetector.MainActivity;

import com.promobile.epilepticdetector.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class ConfiguracaoDaActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	static final int INTENT_CONTACT  = 1;
	private ContatosUtil mContatos;  
	private SharedPreferences prefs_do_contato;
	public static int NUMTYPEPREF;
	
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
		
		mContatos = new ContatosUtil(getContentResolver());
		
		atualizaMeuContatoPreference(1).setTipo(false);
		atualizaMeuContatoPreference(2).setTipo(true);
		atualizaMeuContatoPreference(3).setTipo(true);
		atualizaMeuContatoPreference(4).setTipo(true);
	}
	
	/**
	 * Cria/Atualiza o componente MeuContatoPreference
	 * @param numPref
	 * @return MeuContatoPreference
	 */
	
	public MeuContatoPreference atualizaMeuContatoPreference(int numPref){
		String desNome; 
		String desSummary;

		switch (numPref) {
		   case 1:
			   desNome = "Adicionar contato";
			   desSummary = "Contato que receberá a ligação"; 
		   break;
		   default:
			   desNome = "Adicionar contato";
			   desSummary = "Contato que receberá o sms";
		   break;
		}
		
		SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);
		
		MeuContatoPreference meuContatoPreference = (MeuContatoPreference) getPreferenceScreen().findPreference("pref_key_contato"+numPref);
		meuContatoPreference.setTitle(pref.getString("nomeKey"+numPref, desNome));
        meuContatoPreference.setSummary(pref.getString("telefoneKey"+numPref, desSummary));
        
		return meuContatoPreference;
	}
	
	/**
	 * Guarda em um SharedPreference de nome prefs_do_contato os dados passados por parametro.
	 * @param nome
	 * @param telefone
	 * @param num
	 */
	public void criarPreferenciasContato(String nome, String telefone, int num){
		prefs_do_contato = getSharedPreferences("prefs_do_contato", Context.MODE_PRIVATE);
		Editor editor = prefs_do_contato.edit();
		editor.putString("nomeKey"+num, nome);
	    editor.putString("telefoneKey"+num, telefone);
	    editor.commit();    
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		atualizaMeuContatoPreference(1);	
		atualizaMeuContatoPreference(2);
		atualizaMeuContatoPreference(3);
		atualizaMeuContatoPreference(4);
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	private void setupSimplePreferencesScreen() {
		addPreferencesFromResource(R.xml.pref_general);
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("Notificação");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("Contatos");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_contatos);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		if (key.equals("pref_key_inicio")) {
			Boolean status = sharedPreferences.getBoolean(key, false);
			
				if(status){
					startSensing();	
				
				}else {
					stopSensing();	
				}
			}
		
		if(key.equals("pref_key_perfis")){
				String op = sharedPreferences.getString(key, Integer.toString(EpilepsyHeuristic.PERFIL_MODERADO));
				
				if(op.equals(Integer.toString(EpilepsyHeuristic.PERFIL_MODERADO))){					
	       	 		Toast.makeText(this, "EpilepsyApp - Perfil Moderado selecionado!" , Toast.LENGTH_SHORT).show();
				}
				if(op.equals(Integer.toString(EpilepsyHeuristic.PERFIL_PRECISAO))){
	       	 		Toast.makeText(this, "EpilepsyApp - Perfil Preciso selecionado!" , Toast.LENGTH_SHORT).show();
				}
        }else if (key.equals("pref_key_ringtore")){
        	
        }else if(key.equals("pref_key_contato1")){
        	Intent it = mContatos.getContatoFromIntent();
        	startActivityForResult(it, INTENT_CONTACT);
        	ConfiguracaoDaActivity.NUMTYPEPREF = 1;
        	atualizaMeuContatoPreference(NUMTYPEPREF);	  
        }
        else if(key.equals("pref_key_contato2")){
        	Intent it = mContatos.getContatoFromIntent();
      	    startActivityForResult(it, INTENT_CONTACT);
      	    ConfiguracaoDaActivity.NUMTYPEPREF = 2;	
      	    atualizaMeuContatoPreference(NUMTYPEPREF);
        }
        else if(key.equals("pref_key_contato3")){
        	Intent it = mContatos.getContatoFromIntent();
      	    startActivityForResult(it, INTENT_CONTACT);
      	    ConfiguracaoDaActivity.NUMTYPEPREF = 3;	
      	    atualizaMeuContatoPreference(NUMTYPEPREF);
        }
        else if(key.equals("pref_key_contato4")){
        	Intent it = mContatos.getContatoFromIntent();
      	    startActivityForResult(it, INTENT_CONTACT);
      	    ConfiguracaoDaActivity.NUMTYPEPREF = 4;	
      	    atualizaMeuContatoPreference(NUMTYPEPREF);
        }
	}

	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    switch (requestCode) {
	    case (INTENT_CONTACT):
	      if (resultCode == Activity.RESULT_OK) {
	        Uri contactData = data.getData();
	        Cursor c = getContentResolver().query(
	            contactData, null, null,
	            null, null);
	        List<Contato> contatos = mContatos.getContatosFromCursor(c);
	        if (contatos.size() > 0) {
	        	
	          Contato contato = contatos.get(0);
	          if (contato.telefones.size() > 0) {
	        	  switch (ConfiguracaoDaActivity.NUMTYPEPREF) {
					case 1:
						criarPreferenciasContato(contato.nome, contato.telefones.get(0).numero, NUMTYPEPREF);
					break;
					case 2:
						criarPreferenciasContato(contato.nome, contato.telefones.get(0).numero, NUMTYPEPREF);
					break;
	
					case 3:
						criarPreferenciasContato(contato.nome, contato.telefones.get(0).numero, NUMTYPEPREF);
					break;
					
					case 4:
						criarPreferenciasContato(contato.nome, contato.telefones.get(0).numero, NUMTYPEPREF);
					break;
					
					default:
						break;
					}
	     
	        	  Toast.makeText(this, "Nome: "+contato.nome+" - Telefone: "+contato.telefones.get(0).numero, Toast.LENGTH_SHORT).show();
	          }
	          }
	      }
	      break;
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
		mNotifyManager.cancelAll();
		
		Context context = getApplicationContext();
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	/******************************	BARRA DE NOTIFICAÇÃO ******************************/
	
	@SuppressLint("NewApi")
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