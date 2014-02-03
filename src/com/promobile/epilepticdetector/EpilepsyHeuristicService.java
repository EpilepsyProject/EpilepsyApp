package com.promobile.epilepticdetector;
import com.promobile.epilepticdetector.MainActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class EpilepsyHeuristicService extends Service implements SensorEventListener{

    static String TAG = "EpilepsyHeuristicService";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    
    private EpilepsyHeuristic objHeuristica;
    
    private boolean flagLogs = false;
    
	public IBinder onBind(Intent i) {
		return null;
	}

    public void onCreate() {
		// Inicializando o servico...
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        objHeuristica = new EpilepsyHeuristic(getApplicationContext(), EpilepsyHeuristic.PERFIL_PRECISAO, flagLogs);
        
        // Criando o Servico...
        super.onCreate();
    }
    
    @Override
	public void onDestroy() {
		// Destroindo o servico...
		mSensorManager.unregisterListener(this);
		
		super.onDestroy();
		
		Toast.makeText(this, "EpilepsyApp - Monitoramento desligado!", Toast.LENGTH_SHORT).show();
	}

	public void onStart(Intent intent, int startid) {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);

        Toast.makeText(this, "EpilepsyApp - Monitoramento ligado!", Toast.LENGTH_SHORT).show();
	}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
 
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        int typeSensor = event.sensor.getType();

        // Verificando se esta acontecendo algum desmaio ou ataque epileptico...
    	if(objHeuristica.monitorar(x, y, z, typeSensor))
    	{
    		exibirAlertaDesmaio();
    	}
    }

	public void exibirAlertaDesmaio()
	{
    	Intent dialogIntent = new Intent(this, MainActivity.class);
    	dialogIntent.putExtra("flagDesmaioDetectadoMonitoramento", true);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(dialogIntent);
	}
}
