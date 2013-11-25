package com.promobile.epilepticdetector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AcelerometroActivity extends Activity implements SensorEventListener{

	private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private TextView textViewDetail;
     
    Float x, y, z;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    private String TAG = "logs";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
         
        textViewX = (TextView) findViewById(R.id.txtValorX);
        textViewY = (TextView) findViewById(R.id.txtValorY);
        textViewZ = (TextView) findViewById(R.id.txtValorZ);
        textViewDetail = (TextView) findViewById(R.id.text_view_detail);
         
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        Button btnCriarArquivo = (Button) findViewById(R.id.btnGerarArquivo);
        btnCriarArquivo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CriaArquivosLog(x, y, z);
			}
		});
         
    }
       
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
     
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
     
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
 
    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        
        Log.d(TAG, "X: "+x.toString());
        Log.d(TAG, "Y: "+y.toString());
        Log.d(TAG, "Z: "+z.toString());
          
        textViewX.setText("Posição X: " + x.intValue() + " Float: " + x);
        textViewY.setText("Posição Y: " + y.intValue() + " Float: " + y);
        textViewZ.setText("Posição Z: " + z.intValue() + " Float: " + z);
         
        if(y < 0) { // O dispositivo esta de cabeça pra baixo
            if(x > 0)  
                textViewDetail.setText("Virando para ESQUERDA ficando INVERTIDO");
            if(x < 0)  
                textViewDetail.setText("Virando para DIREITA ficando INVERTIDO");   
        } else {
            if(x > 0)  
                textViewDetail.setText("Virando para ESQUERDA ");
            if(x < 0)  
                textViewDetail.setText("Virando para DIREITA ");
        }
        CriaArquivosLog(x, y, z);
    }
    public void CriaArquivosLog(Float x, Float y, Float z ){
    	try {
    		File arq = new File(Environment.getExternalStorageDirectory(), "logs.txt");
			FileOutputStream escrever = new FileOutputStream(arq,true);
			
			escrever.write(x.toString().getBytes());
			escrever.write(",".getBytes());
			escrever.write(y.toString().getBytes());
			escrever.write(",".getBytes());
			escrever.write(z.toString().getBytes());
			escrever.write(";".getBytes());
			escrever.write("\n".getBytes());
			escrever.flush();
			escrever.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
