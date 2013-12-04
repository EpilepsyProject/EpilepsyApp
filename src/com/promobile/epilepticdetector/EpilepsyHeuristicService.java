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

public class EpilepsyHeuristicService extends Service implements SensorEventListener{

	private long miliTimeInicial;
	private TextView textViewTimer;
	private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private TextView textViewVetor;
    private TextView textViewDetail;
    private TextView textViewDesmaio;
    
    //******** Begin: Estagios da deteccao de desmaio
    private Boolean flagEstagio1;
    private Boolean flagEstagio2;
    private Boolean flagEstagio3;
    private Integer contPrecisao;
    //******** End: Estagios da deteccao de desmaio
    
    Float x, y, z;
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

    
    public void onCreate()
    {
        super.onCreate();
        
        initialTime = System.currentTimeMillis();
		Log.d(TAG, "onCreate");

//		FILEPATH = new File(Environment.getExternalStorageDirectory().getPath()+"/TeddySensing/");
//		FILEPATH.mkdirs();
//		Log.e(TAG, "Path is :" + FILEPATH);
//		alive = true;
//		prepareFiles();

		miliTimeInicial = System.currentTimeMillis();

//        textViewTimer = (TextView) findViewById(R.id.txtValorTimer);
//        textViewX = (TextView) findViewById(R.id.txtValorX);
//        textViewY = (TextView) findViewById(R.id.txtValorY);
//        textViewZ = (TextView) findViewById(R.id.txtValorZ);
//        textViewVetor = (TextView) findViewById(R.id.txtValorVetor);
//        textViewDetail = (TextView) findViewById(R.id.text_view_detail);
//        textViewDesmaio = (TextView) findViewById(R.id.text_view_desmaio);
         
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      
        /********************************************************************************
         *						HEURISTICA DE DETECCAO DE DESMAIO						*
         ********************************************************************************/
        // Inicializando constantes...
        flagEstagio1 = false;
        flagEstagio2 = false;
        flagEstagio3 = false;
        contPrecisao = 0;
         
        // Obtendo instante inicial do log...
        miliTimeInicial = System.currentTimeMillis();
    }
    
    
    public void prepareFiles(){
//		//create a new file for gyro
//		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);	
//		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  SensorManager.SENSOR_DELAY_FASTEST);		
//
//		//create a new file for accelorometer
//		try {
//			Log.e("Log","Write");
//			file1 = FILEPATH+"/"+miliTimeInicial+"-accl.txt";
//			fout1 = new FileOutputStream(file1,true);
//		} catch (IOException e) {
//			Log.e("IOError",e.toString());
//		}
//		//set up recorder
//		/*
//		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
//		AUDIO_RECORDER_FILE_EXT_WAV = FILEPATH+"/"+miliTimeInicial+".wav";
//		AUDIO_RECORDER_TEMP_FILE = FILEPATH+"/"+miliTimeInicial+"-temp.raw"; 			
//		startRecording();
//		*/
	}
          
   
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
		
		// Instante de tempo que o log foi gerado...
		double miliTimeAtual = (System.currentTimeMillis() - miliTimeInicial) / 1000.0;
		
		// Calculando os modulos resultantes dos eixos x, y e z
		double moduloVetorAceleracao = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        
        /**TODO: Rawlinson - Comentado temporariamente...
        Log.d(TAG, "X: "+x.toString());
        Log.d(TAG, "Y: "+y.toString());
        Log.d(TAG, "Z: "+z.toString());
        **/
         
        textViewTimer.setText("Timer: " + miliTimeAtual);
        textViewX.setText("Posicao X: " + x.intValue() + " Float: " + x);
        textViewY.setText("Posicao Y: " + y.intValue() + " Float: " + y);
        textViewZ.setText("Posicao Z: " + z.intValue() + " Float: " + z);
        textViewVetor.setText("Vetor Aceleracao: " + Double.toString(moduloVetorAceleracao));
         
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
        
        /********************************************************************************
         *						HEURISTICA DE DETECCAO DE DESMAIO						*
         ********************************************************************************/
        if(moduloVetorAceleracao <= 6.0)
        {
            flagEstagio1 = true;
            
            // Obter tempo em milisegundos... para estimar com mais precisao o processo de queda...
            //mintime = System.currentTimeMillis();
        }

        if(flagEstagio1 == true)
        {
        	textViewDesmaio.setText("");
        	
        	contPrecisao++;
        	if(moduloVetorAceleracao >= 13.5)
        	{
        		flagEstagio2 = true;
        	}
        }
        
        if(flagEstagio1 == true && flagEstagio2 == true)
        {
        	// Verificar se o cara apagou... atraves de uma margem de erro em relacao a normal 9,8 
        	
        	flagEstagio3 = true;
        	// exibir alert da larissa...
        	textViewDesmaio.setText("***************** DESMAIO DETECTADO *****************");
	        
        	contPrecisao = 0;
        	flagEstagio1 = false;
        	flagEstagio2 = false;
        	flagEstagio3 = false;
        }

		if( contPrecisao > 10)
		{
		    contPrecisao = 0;
		    flagEstagio1 = false;
		    flagEstagio2 = false;
		    flagEstagio3 = false;
		}
        
        // Gerando os logs do sistema...
        //TODO: Retirado temporariamente//CriaArquivosLog(miliTimeAtual, x, y, z, moduloVetorAceleracao);
    }
    
    
//	@Override
//	protected void onResume() {
//	    super.onResume();
//	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
//	}
//	 
//	@Override
//	protected void onPause() {
//	    super.onPause();
//	    mSensorManager.unregisterListener(this);
//	}

    public void CriaArquivosLog(Double miliTimeAtual, Float x, Float y, Float z, Double moduloVetorAceleracao)
    {
    	try
    	{
    		/** BEGIN: Salvando os dados do acelerometro em um log dentro do CARD SD... **/
    		File arq = new File(Environment.getExternalStorageDirectory(), "logs_" + miliTimeInicial + ".txt");
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
    		File arqLog = new File(Environment.getExternalStorageDirectory(), "logGraficoAcelerometro_" + miliTimeInicial + ".txt");
			FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			escreverLog.write(Double.toString(miliTimeAtual).getBytes());
			escreverLog.write(";".getBytes());

			// Calculando os modulos resultantes dos eixos x, y e z
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
		Log.d(TAG, "onDestroy");
		alive=false;
//		finishFiles();
		
		pauseThread = null;
	}

//	public void finishFiles(){
//		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));				
//
//		try{
//			//	fout2.flush();
//			fout2.close();
//			//	fout1.flush();
//			fout1.close();
//			//	fout3.flush();
//			fout3.close();
//		}catch(IOException e){
//			Log.e("IOException","Error on closing files");
//		}
//
//	//	stopRecording();
//	}

	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
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
						//finishFiles();
						//prepareFiles();
					}
					//finishFiles();
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