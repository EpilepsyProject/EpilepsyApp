package com.promobile.epilepticdetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class AcelerometroActivity extends Service implements SensorEventListener{

	private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private TextView textViewDetail;
    private long miliTimeInicial;
    
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    static String TAG = "AcelerometroActivity";
    
    private long initialTime;
	
	private SensorManager sensorManager;
	static String file1,file2,file3;
	static FileOutputStream fout1, fout2, fout3;
	public static File FILEPATH;
	private float[] mGravs = new float[3];


	public volatile Thread pauseThread;
	public static boolean alive;

	public IBinder onBind(Intent i) {
		return null;
	}

    
    public void onCreate() {
        super.onCreate();
        initialTime = System.currentTimeMillis();
		Log.e(TAG, "onCreate");

		FILEPATH = new File(Environment.getExternalStorageDirectory().getPath()+"/TeddySensing/");
		FILEPATH.mkdirs();
		Log.e(TAG, "Path is :" +FILEPATH);
		alive = true;
		prepareFiles();
		 miliTimeInicial = System.currentTimeMillis();
      //  setContentView(R.layout.activity_acelerometro);
         
      //  textViewX = (TextView) findViewById(R.id.txtValorX);
      //  textViewY = (TextView) findViewById(R.id.txtValorY);
      //  textViewZ = (TextView) findViewById(R.id.txtValorZ);
     //   textViewDetail = (TextView) findViewById(R.id.text_view_detail);
      
    }
    public void prepareFiles(){
		//create a new file for gyro
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);	
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  SensorManager.SENSOR_DELAY_FASTEST);		

		//create a new file for accelorometer
		try {
			Log.e("Log","Write");
			file1 = FILEPATH+"/"+getCurrentTimeStamp()+"-accl.txt";
			fout1 = new FileOutputStream(file1,true);
		} catch (IOException e) {
			Log.e("IOError",e.toString());
		}
		//set up recorder
/*		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
		AUDIO_RECORDER_FILE_EXT_WAV = FILEPATH+"/"+getCurrentTimeStamp()+".wav";
		AUDIO_RECORDER_TEMP_FILE = FILEPATH+"/"+getCurrentTimeStamp()+"-temp.raw"; 			
		startRecording();*/
	}
          
   
    @Override
    public void onSensorChanged(SensorEvent event) {
    	float[] values_accel = new float[3];		
		
		//long pastTime = actualTime - initialTime;
		long pastTime = initialTime;
		String string;
		 double miliTimeAtual = (System.currentTimeMillis() - miliTimeInicial) / 1000.0;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			values_accel = event.values;
			for (int i=0;i<3;i++) mGravs[i] = event.values[i];
			float x = values_accel[0]; //Acceleration force along the x axis (including gravity).
			float y = values_accel[1]; //Acceleration force along the y axis (including gravity).
			float z = values_accel[2]; //Acceleration force along the z axis (including gravity).
			try {
				string = ""+pastTime+" "+x+" "+y+" "+z+"\n";
				fout1.write(string.getBytes());
				fout1.flush();
			} catch (IOException e) {
				Log.e("IOError",e.toString());
			}
			   CriaArquivosLog(miliTimeAtual, x, y, z);
		}
       /*  Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];
        
       Log.d(TAG, "X: "+x.toString());
        Log.d(TAG, "Y: "+y.toString());
        Log.d(TAG, "Z: "+z.toString());*/
        
         /*
        Os valores ocilam de -10 a 10.
        Quanto maior o valor de X mais ele ta caindo para a esquerda - Positivo Esqueda 
        Quanto menor o valor de X mais ele ta caindo para a direita  - Negativo Direita
        Se o valor de  X for 0 ent�o o celular ta em p� - Nem Direita Nem Esquerda
        Se o valor de Y for 0 ent�o o cel ta "deitado"
         Se o valor de Y for negativo ent�o ta de cabe�a pra baixo, ent�o quanto menor y mais ele ta inclinando pra ir pra baixo
        Se o valor de Z for 0 ent�o o dispositivo esta reto na horizontal.
        Quanto maioro o valor de Z Mais ele esta inclinado para frente
        Quanto menor o valor de Z Mais ele esta inclinado para traz.
        */
    /*    textViewX.setText("Posi��o X: " + x.intValue() + " Float: " + x);
        textViewY.setText("Posi��o Y: " + y.intValue() + " Float: " + y);
        textViewZ.setText("Posi��o Z: " + z.intValue() + " Float: " + z);
        long pastTime = initialTime;
		String string;
		
        if(y < 0) { // O dispositivo esta de cabe�a pra baixo
            if(x > 0)  
                textViewDetail.setText("Virando para ESQUERDA ficando INVERTIDO");
            if(x < 0)  
                textViewDetail.setText("Virando para DIREITA ficando INVERTIDO");   
        } else {
            if(x > 0)  
                textViewDetail.setText("Virando para ESQUERDA ");
            if(x < 0)  
                textViewDetail.setText("Virando para DIREITA ");
        
	        try {
				string = ""+pastTime+" "+x+" "+y+" "+z+"\n";
				fout1.write(string.getBytes());
				fout1.flush();
			} catch (IOException e) {
				Log.e("IOError",e.toString());
			}
        }*/
    }
    
   /* protected void onResume() {
        
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
     
    protected void onPause() {
        
        mSensorManager.unregisterListener(this);
    }
    */
 

public void CriaArquivosLog(Double miliTimeAtual, Float x, Float y, Float z )
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
                    escreverLog.write(Double.toString(miliTimeAtual).getBytes());
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
    @Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		alive=false;
		finishFiles();
		pauseThread = null;

	}

	public void finishFiles(){
		
		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));				

		try{
			//	fout2.flush();
			fout2.close();
			//	fout1.flush();
			fout1.close();
			//	fout3.flush();
			fout3.close();
		}catch(IOException e){
			Log.e("IOException","Error on closing files");
		}

	//	stopRecording();
	}

	public void onStart(Intent intent, int startid) {
		Log.e(TAG, "onStart");
		if(pauseThread == null){
			pauseThread = new Thread(new Runnable() {
				public void run() {
					while(alive){
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finishFiles();
						prepareFiles();
					}
					finishFiles();
				}
			});
			pauseThread.start();
		}
	}

     
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}
}
