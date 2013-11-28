package com.promobile.epilepticdetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class GiroscopioActivity extends Activity implements SensorEventListener{

	private TextView txtValorX;
	private TextView txtValorY;
	private TextView txtValorZ;
	private SensorManager mSensorManager;
	private Sensor mGyroscope;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giroscopio);
		txtValorX = (TextView) findViewById(R.id.ValorX);
		txtValorY = (TextView) findViewById(R.id.ValorY);
		txtValorZ = (TextView) findViewById(R.id.ValorZ);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		txtValorX.setText(String.valueOf(event.values[0]));
		txtValorY.setText(String.valueOf(event.values[1]));
		txtValorZ.setText(String.valueOf(event.values[2]));
		
		CriaArquivosLog(event.values[0], event.values[1], event.values[2]);
		
	}
		
	public void CriaArquivosLog(Float x, Float y, Float z )
    {
    	try
    	{
    		/** BEGIN: Salvando os dados do Giroscópio em um log dentro do CARD SD... **/
    		File arq = new File(Environment.getExternalStorageDirectory(), "logsgyro.txt");
			FileOutputStream escrever = new FileOutputStream(arq, true);
			
			escrever.write(x.toString().getBytes());
			escrever.write(";".getBytes());
			escrever.write(y.toString().getBytes());
			escrever.write(";".getBytes());
			escrever.write(z.toString().getBytes());
			escrever.write("\n".getBytes());
			escrever.flush();
			escrever.close();
    		/** END: Salvando os dados do Giroscópio em um log dentro do CARD SD... **/
			
		}
    	catch (IOException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}