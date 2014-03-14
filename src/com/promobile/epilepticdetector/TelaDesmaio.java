package com.promobile.epilepticdetector;

import com.promobile.democonfig.ConfiguracaoDaActivity;
import com.promobile.democonfig.MeuContatoPreference;
import com.promobile.epilepticdetector.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TelaDesmaio extends Activity {
 
		// Intervalo em milisegundos
		private final int INTERVAL = 1000;
		// Duração em milisegundos
		private int DURATION = 6000;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_tela_desmaio);
			DURATION = getDuration();
		}
		
		@Override
		protected void onStart() {
			DURATION = getDuration();
			super.onStart();
		}

		public int getDuration(){
			SharedPreferences prefAlerta = PreferenceManager.getDefaultSharedPreferences(this);
			String alerta = prefAlerta.getString("pref_key_alerta", "");
			return Integer.valueOf(alerta)*INTERVAL;
		}
		
		public void telaconfigurcoes(View view){
			Intent intent = new Intent(this, ConfiguracaoDaActivity.class);
			startActivity(intent);
		}
		
		public void showDialogClick(View v) {
			
			// Prepara o Dialog informando o título, mensagem e cria o Positive Button        
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
	        alertBuilder.setTitle(R.string.title);
	        alertBuilder.setMessage("Um possível demaio foi detectado! Enviando SMS ao fim da contagem...");
	        
	        // Aqui você pode definir a ação de clique do botão
	        alertBuilder.setPositiveButton("OK", null);
	                
	        // Criar o AlertDialog
	        final AlertDialog alert = alertBuilder.create();
	        
	        alert.show();
	        
	        // Cria um objeto da classe CountDownTimer 
	        // informando a duração e o intervalo
	        new CountDownTimer(DURATION, INTERVAL) {
				
				// A cada 1 segundo o onTick é invocado
	        	@Override
				public void onTick(long millisUntilFinished) {
	        		// Pega o botão do Alert
					Button btn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
					// Formata o texto do botão com os segundos. 
					// Ex. OK (9)
					btn.setText(("Cancelar envio de SMS? ("+(millisUntilFinished/INTERVAL)) + ")");
				}
				
				@Override
				public void onFinish() {
					// Fecha o alert
					for (int i = 2; i <= 4; i++) {
						SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);
						String destino = pref.getString("telefoneKey"+i, "Nda");
						
						if(!destino.equals("Nda")){
							enviaSms(destino);
						}
					}
					SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);
					String destino = pref.getString("telefoneKey"+1, "Nda");
					
					Uri uri = Uri.parse("tel:"+ destino);
					Intent intent = new Intent(Intent.ACTION_DIAL,uri);
					startActivity(intent);

					alert.dismiss();
				}
			}.start();
		}
		
		public void enviaSms(String destino){
			
			String phoneNumber = destino;
			String mensagem = "Teste envio SMS numero ";
			
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, mensagem, null, null);
			
			Toast.makeText(getApplicationContext(), "EpilepsyApp - Mensagem Enviada para "+destino, Toast.LENGTH_SHORT).show();

		}
}
