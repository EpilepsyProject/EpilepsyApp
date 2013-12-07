package com.promobile.epilepticdetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity  {
	

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ImageButton inicio = (ImageButton) findViewById(R.id.imageButton2);
            ImageButton config = (ImageButton) findViewById(R.id.imageButton1);
            
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
        }
     }
