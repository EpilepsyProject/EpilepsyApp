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
		// Dura��o em milisegundos
		private final int DURATION = 6000;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
		}

		public void showDialogClick(View v) {
			
			// Prepara o Dialog informando o t�tulo, mensagem e cria o Positive Button        
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
	        alertBuilder.setTitle(R.string.title);
	        alertBuilder.setMessage(R.string.message);
	        
	        // Aqui voc� pode definir a a��o de clique do bot�o
	        alertBuilder.setPositiveButton("OK", null);
	                
	        // Criar o AlertDialog
	        final AlertDialog alert = alertBuilder.create();
	        
	        alert.show();
	        
	        // Cria um objeto da classe CountDownTimer 
	        // informando a dura��o e o intervalo
	        new CountDownTimer(DURATION, INTERVAL) {
				
				// A cada 1 segundo o onTick � invocado
	        	@Override
				public void onTick(long millisUntilFinished) {
	        		// Pega o bot�o do Alert
					Button btn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
					// Formata o texto do bot�o com os segundos. 
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
