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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GiroscopioActivity extends Activity implements SensorEventListener{

	private TextView txtValorX;
	private TextView txtValorY;
	private TextView txtValorZ;
	private SensorManager mSensorManager;
	private Sensor mGyroscope;
	private static final double NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private double timestamp;


	Float x, y, z;
		
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
		float axisX;
		float axisY;
		float axisZ;
		// This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final double dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            axisX = event.values[0];
            axisY = event.values[1];
            axisZ = event.values[2];

            // Calculate the angular speed of the sample
            double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            if (omegaMagnitude > 0) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            double thetaOverTwo = omegaMagnitude * dT / 2.0f;
            double sinThetaOverTwo = Math.sin(thetaOverTwo);
            double cosThetaOverTwo = Math.cos(thetaOverTwo);
            deltaRotationVector[0] = (float) sinThetaOverTwo * axisX;
            deltaRotationVector[1] = (float) sinThetaOverTwo * axisY;
            deltaRotationVector[2] = (float) sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = (float) cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
		x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        txtValorX.setText("Posicao X: " + Float.toString(axisX));
        txtValorY.setText("Posicao Y: " + axisY);
        txtValorZ.setText("Posicao Z: " + axisZ);
		
		//CriaArquivosLog(event.values[0], event.values[1], event.values[2]);
		
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