package com.promobile.epilepticdetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlertasActivity extends Activity {

	//Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alertas);
		Button button = (Button) findViewById(R.id.button);		
		
		button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Configuração de alertas salvo!",
						Toast.LENGTH_SHORT).show();								
			}
		});
		
	}

}
