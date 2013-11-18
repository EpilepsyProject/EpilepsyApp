package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

public class TelaDesmaio extends Activity {
 
	// Intervalo em milisegundos
		private final int INTERVAL = 1000;
		// Duração em milisegundos
		private final int DURATION = 6000;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
		}

		public void showDialogClick(View v) {
			
			// Prepara o Dialog informando o título, mensagem e cria o Positive Button        
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
	        alertBuilder.setTitle(R.string.title);
	        alertBuilder.setMessage(R.string.message);
	        
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
					alert.dismiss();
				}
			}.start();
		}
	}
