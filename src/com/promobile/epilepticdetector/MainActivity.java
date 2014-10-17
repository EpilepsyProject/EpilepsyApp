package com.promobile.epilepticdetector;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.promobile.configuracoes.ConfiguracaoDaActivity;
import com.promobile.contatos.Contato;
/**
 * Esta classe é reponsável por controlar a tela principal do aplicativo.
 * @author eribeiro
 *
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity  {
	Boolean flagDesmaioDetectadoMonitoramento = false;
    // Iniciando objetos de musica do android...
    Ringtone objRing;
    Button btnAlertDesmaio;
    /**
     * Variavel utilizadas na localizacao
     */
    GPSTracker gpsTracker;
    Contato contato;
    /**
     * Constante com o valor do tempo em milisegundos que a mensagem de desmaio será mostrada ao usuário.<br>
     * INTERVAL = 1000  
     */
    private final int INTERVAL = 1000;
	int contSms = 0;
	
 	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);	 
               
        /** Verificando se houve algum desmaio detectado pelo sistema de monitoramento... **/
        if(getIntent().hasExtra("flagDesmaioDetectadoMonitoramento")) {
        	Bundle extras = getIntent().getExtras();
        	flagDesmaioDetectadoMonitoramento = extras.getBoolean("flagDesmaioDetectadoMonitoramento");
			if(flagDesmaioDetectadoMonitoramento == true)
			{
				showDialogDesmaio();
			}
        }
        else{
        	flagDesmaioDetectadoMonitoramento = false;
        }
 	}
 	
 	@Override
 	protected void onResume() {
 		super.onResume();
 	}
 
 	/***
 	 * Método responsável por gerar uma mensagem que será mostrada ao usuario informando que um possivel desmaio foi detectado.
 	 */
	public void showDialogDesmaio(){
		contato = new Contato(this);
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
	    Uri objNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	    objRing = RingtoneManager.getRingtone(getApplicationContext(), objNotification);

	    // Pega o botão do Alert
	    btnAlertDesmaio = alert.getButton(AlertDialog.BUTTON_POSITIVE);

	    // Objeto vibracao...
	    final Vibrator objVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// Start without a delay
		// Each element then alternates between vibrate, sleep, vibrate, sleep...
		long[] pattern = {300, 100, 1000, 300, 200, 100, 500, 200, 100};
		objVibrator.vibrate(pattern, 0);
		
	    // Cria um objeto da classe CountDownTimer
	    // informando a duração e o intervalo
	    final CountDownTimer timerDialog = new CountDownTimer(getDuration(), INTERVAL) {

	    // A cada 1 segundo o onTick é invocado
	    @Override
	    public void onTick(long millisUntilFinished) {
	    	// Formata o texto do botão com os segundos.
	    	// Ex. OK (9)
	        btnAlertDesmaio.setText(("Cancelar envio de SMS? ("+(millisUntilFinished/INTERVAL)) + ")");

	        // 	Toca o alerta sonoro...
			try {
				if(!objRing.isPlaying()){
				objRing.play();
				}
			}catch (Exception e) {}
		}

		@Override
		public void onFinish(){
			contato.enviarSmsParaContatos();
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
			}catch (Exception e) {}
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
        	Intent intent = new Intent(MainActivity.this, ConfiguracaoDaActivity.class);
     		startActivity(intent);
        	return true;
        }
        case (R.id.action_testes):{
        	Intent intent = new Intent(MainActivity.this, InicialActivity.class);
            startActivity(intent);
        	return true;
        }
        default:
        	 return super.onOptionsItemSelected(item);
    	}
    }  
    /**
     * Método que retorna a duração de tempo que o alerta de posível desmaio será mostrado ao usuário.
     * @return tempo em milisegundos
     */
    public int getDuration(){
		SharedPreferences prefAlerta = PreferenceManager.getDefaultSharedPreferences(this);
		String alerta = prefAlerta.getString("pref_key_alerta", "");
		return Integer.valueOf(alerta)*INTERVAL;
	}
    
    
}