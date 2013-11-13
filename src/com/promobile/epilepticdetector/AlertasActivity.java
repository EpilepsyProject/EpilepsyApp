package com.promobile.epilepticdetector;

import br.com.promobile.epilepticdetector.banco.HelpSharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class AlertasActivity extends Activity {

	Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alertas);
		Button button;
		button = (Button) findViewById(R.id.button);
		
		SharedPreferences preferences = getSharedPreferences("pref_config", MODE_PRIVATE);
		HelpSharedPreferences configuracao = new HelpSharedPreferences(preferences);
		//configuracao.setTempo(60);
		//configuracao.getTempo();
		
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alertas, menu);
		Context contexto = getApplicationContext(); String texto = "Salvo com sucesso!";
		int duracao = Toast.LENGTH_SHORT; 
		Toast toast = Toast.makeText(contexto, texto,duracao); 
		toast.show();
		
		return true;
	}

}
