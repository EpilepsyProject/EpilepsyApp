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

	private long miliTimeInicial;

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
        
        /**TODO: RAWLINSON - NÌO PRECISA DESSE BOTAO...
        Button btnCriarArquivo = (Button) findViewById(R.id.btnGerarArquivo);
        btnCriarArquivo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CriaArquivosLog(x, y, z);
			}
		});
		**/
        
        // Obtendo instante inicial do log...
        miliTimeInicial = System.currentTimeMillis();
    }
       
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
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
        
        /**TODO: Rawlinson - Comentado tempor‡riamente...
        Log.d(TAG, "X: "+x.toString());
        Log.d(TAG, "Y: "+y.toString());
        Log.d(TAG, "Z: "+z.toString());
        **/
          
        textViewX.setText("Posicao X: " + x.intValue() + " Float: " + x);
        textViewY.setText("Posicao Y: " + y.intValue() + " Float: " + y);
        textViewZ.setText("Posicao Z: " + z.intValue() + " Float: " + z);
         
        if(y < 0) { // O dispositivo esta de cabeca pra baixo
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

    public void CriaArquivosLog(Float x, Float y, Float z )
    {
    	try
    	{
    		/** BEGIN: Salvando os dados do acelerometro em um log dentro do CARD SD... **/
    		File arq = new File(Environment.getExternalStorageDirectory(), "logs.txt");
			FileOutputStream escrever = new FileOutputStream(arq, true);
			
			escrever.write(x.toString().getBytes());
			escrever.write(";".getBytes());
			escrever.write(y.toString().getBytes());
			escrever.write(";".getBytes());
			escrever.write(z.toString().getBytes());
			escrever.write("\n".getBytes());
			escrever.flush();
			escrever.close();
    		/** END: Salvando os dados do acelerometro em um log dentro do CARD SD... **/
			
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		File arqLog = new File(Environment.getExternalStorageDirectory(), "logGraficoAcelerometro.txt");
			FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			long miliTimeAtual = System.currentTimeMillis();
			escreverLog.write(Long.toString(miliTimeAtual - miliTimeInicial).getBytes());
			escreverLog.write(";".getBytes());
			
			// Calculando os modulos resultantes dos eixos x, y e z
			double moduloVetorAceleracao = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
			escreverLog.write(Double.toString(moduloVetorAceleracao).getBytes());
			escreverLog.write("\n".getBytes());
			
			escreverLog.flush();
			escreverLog.close();
    		/** END: Gerando grafico de analise do acelerometro **/
		}
    	catch (IOException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}