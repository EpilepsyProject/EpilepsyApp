package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity  {

	Boolean flagDesmaioDetectadoMonitoramento = false;
	
    // Iniciando objetos de musica do android...
	Uri objNotification;
    Ringtone objRing;
    Button btnAlertDesmaio;
    
	// Intervalo em milisegundos
	private final int INTERVAL = 1000;
	
	// Duração em milisegundos
	private final int DURATION = 60000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton inicio = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton config = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton perfil = (ImageButton) findViewById(R.id.imageButton3);
        
        inicio.setOnClickListener(new OnClickListener() {
        	 
			@Override
			public void onClick(View arg0) {
     			
			  Intent intent = new Intent(MainActivity.this, InicialActivity.class);
			  startActivity(intent);
 
			}
 
		});
        
        config.setOnClickListener(new OnClickListener() {
       	 
			@Override
			public void onClick(View arg0) {
     			
			  Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
			  startActivity(intent);
 
			}
 
		});
        
        perfil.setOnClickListener(new OnClickListener() {
          	 
			@Override
			public void onClick(View arg0) {
     			
			  Intent intent = new Intent(MainActivity.this, TipoPerfil.class);
			  startActivity(intent);
 
			}
 
		});
        
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
		
        // Cria um objeto da classe CountDownTimer 
        // informando a duração e o intervalo
        final CountDownTimer timerDialog = new CountDownTimer(DURATION, INTERVAL) {

			// A cada 1 segundo o onTick é invocado
        	@Override
			public void onTick(long millisUntilFinished) {
 				// Formata o texto do botão com os segundos. 
				// Ex. OK (9)
        		btnAlertDesmaio.setText(("Cancelar envio de SMS? ("+(millisUntilFinished/INTERVAL)) + ")");

		        // Toca o alerta sonoro...
		        try {
		        	if(!objRing.isPlaying())
		        	{
		        		objRing.play();
		        	}
		        } catch (Exception e) {}
			}
			
			@Override
			public void onFinish() {
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
		        } catch (Exception e) {}
			}
		});
	}
}
