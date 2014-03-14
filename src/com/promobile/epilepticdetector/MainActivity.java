package com.promobile.epilepticdetector;

import com.promobile.demolocalizacao.GPSTracker;

import com.promobile.democonfig.ConfiguracaoDaActivity;
import com.promobile.epilepticdetector.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity  {
	Boolean flagDesmaioDetectadoMonitoramento = false;
	
    // Iniciando objetos de musica do android...
	Uri objNotification;
    Ringtone objRing;
    Button btnAlertDesmaio;
    /**
     * Variaveis utilizadas na localizacao
     */
    GPSTracker gpsTracker;
    private double latitude, longitude;
    private String endereço;
    
    // Intervalo em milisegundos
    private final int INTERVAL = 1000;

    // Duração em milisegundos
    private int DURATION = 60000;
	int contSms = 0;
	
	 	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);	      
	       /**
	        * Se o GPS está desativado emite um alerta
	        */
	       /* gpsTracker = new GPSTracker(this);
	        Toast.makeText(this, ""+gpsTracker.canGetLocation(), Toast.LENGTH_LONG).show();
	        if(!gpsTracker.canGetLocation()){
	        	gpsTracker.showSettingsAlert();
	        }
	        */
	        
	        /** Verificando se houve algum desmaio detectado pelo sistema de monitoramento... **/
	        if(getIntent().hasExtra("flagDesmaioDetectadoMonitoramento")) {
	        	Bundle extras = getIntent().getExtras();
	        	flagDesmaioDetectadoMonitoramento = extras.getBoolean("flagDesmaioDetectadoMonitoramento");
				if(flagDesmaioDetectadoMonitoramento == true)
				{
					showDialogDesmaio();
				}
	        }
	        else
	        {
	        	flagDesmaioDetectadoMonitoramento = false;
	        }
	    }
	 	@Override
	 	protected void onResume() {
	 		//latitude = gpsTracker.getLatitude();
	 		//longitude = gpsTracker.getLongitude();	 		
	 		
	 		super.onResume();
	 	}

		public void showDialogDesmaio()
		{
		    // Prepara o Dialog informando o título, mensagem e cria o Positive Button
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		    alertBuilder.setTitle(R.string.title);
		    alertBuilder.setMessage("Um possível demaio foi detectado! Enviando SMS ao fim da contagem...");
		        
		    // Aqui você pode definir a ação de clique do botão
		    alertBuilder.setPositiveButton("OK", null);
		                
		    // Criar o AlertDialog
		    final AlertDialog alert = alertBuilder.create();
		        
		    alert.show();
		        
		    // Iniciando objetos de musica do android...
		    objNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		    objRing = RingtoneManager.getRingtone(getApplicationContext(), objNotification);
	
		    // Pega o botão do Alert
		    btnAlertDesmaio = alert.getButton(AlertDialog.BUTTON_POSITIVE);
	
		    // Objeto vibracao...
		    final Vibrator objVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	
			// Start without a delay
			// Each element then alternates between vibrate, sleep, vibrate, sleep...
			long[] pattern = {300, 100, 1000, 300, 200, 100, 500, 200, 100};
			objVibrator.vibrate(pattern, 0);
			
			DURATION = getDuration();
			
		    // Cria um objeto da classe CountDownTimer
		    // informando a duração e o intervalo
		    final CountDownTimer timerDialog = new CountDownTimer(DURATION, INTERVAL) {
	
		    // A cada 1 segundo o onTick é invocado
		    @Override
		    public void onTick(long millisUntilFinished) {
		    	// Formata o texto do botão com os segundos.
		    	// Ex. OK (9)
		        btnAlertDesmaio.setText(("Cancelar envio de SMS? ("+(millisUntilFinished/INTERVAL)) + ")");
	
		        // 	Toca o alerta sonoro...
				try {
					if(!objRing.isPlaying())
					{
					objRing.play();
					}
				}catch (Exception e) {}
			}

			@Override
			public void onFinish() {
				//Manda um sms para cada contato cadastrado na tela de configurações 
				
				for (int i = 2; i <= 4; i++) {
					SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);
					String destino = pref.getString("telefoneKey"+i, "Nda");
					
					if(!destino.equals("Nda")){
						enviaSms(destino);
					}
				}
				SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);
				String destino = pref.getString("telefoneKey"+1, "Nda");
				
				if(!destino.equals("Nda")){
					ligarContato(destino);
				}
				
				// Fecha o alert
				alert.dismiss();
			}
		    }.start();
	        
		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(final DialogInterface dialog) {
			// Para o alerta sonoro...
			try {
				timerDialog.cancel(); // Para o contador do alerte...
			
				objVibrator.cancel(); // Faz o celular parar de vibrar...
			
				objRing.stop(); // Para de tocar a musica...
			}catch (Exception e) {
				
			}
		}
		});
		}
	 
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.activity_main_actions, menu);
	 
	        return super.onCreateOptionsMenu(menu);
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
            
            case (R.id.action_config):{		            	
            	returnConfig();		
            	return true;
            }
            case (R.id.action_testes):{
            	returnInicial();
            	return true;
            }
            default:
            	 return super.onOptionsItemSelected(item);
	    	}
	    }  
	        
	    void returnInicial () {
	         Intent intent = new Intent(MainActivity.this, InicialActivity.class);
	         startActivity(intent);
	    }	    	    

	    void returnConfig (){
	    	Intent intent = new Intent(MainActivity.this, ConfiguracaoDaActivity.class);
	 		startActivity(intent);
	    }
	    
	    /**
	     * Funcoes para a envio de Sms e configuracoes do tempo do alerta
	     */
	    
	    public int getDuration(){
			SharedPreferences prefAlerta = PreferenceManager.getDefaultSharedPreferences(this);
			String alerta = prefAlerta.getString("pref_key_alerta", "");
			return Integer.valueOf(alerta)*INTERVAL;
		}
	    
	    public void enviaSms(String destino){
			
			String phoneNumber = destino;
			String mensagem = "Acabei de sofrer um ataque epilético";
			
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, mensagem, null, null);
			
			Toast.makeText(getApplicationContext(), "EpilepsyApp - Mensagem Enviada para "+destino, Toast.LENGTH_SHORT).show();

		}
	    
	    public void ligarContato(String destino){
	    	Uri uri = Uri.parse("tel:"+ destino);
			Intent intent = new Intent(Intent.ACTION_DIAL,uri);
			startActivity(intent);
	    }
}
