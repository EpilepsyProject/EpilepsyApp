package com.promobile.epilepticdetector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Stack;

import android.R.array;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AcelerometroActivity extends Activity implements SensorEventListener{

	private long miliTimeInicial;
	private TextView textViewTimer;
	private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
	private TextView textViewXG;
    private TextView textViewYG;
    private TextView textViewZG;
    private TextView textViewVetor;
    private TextView textViewDetail;
    private TextView textViewDesmaio;
    private TextView textViewStatus;
    
    /************* Estagios da deteccao de desmaio ******************/

    // Iniciando objetos de musica do android...
	Uri objNotification;
    Ringtone objRing;
    Button btnAlertDesmaio;

    private Boolean flagGyroscopeAtivado = false;
    
    private Boolean flagEstagio1 = false;
    private Boolean flagEstagio2 = false;
    private Boolean flagEstagio3 = false;
    private Boolean flagEstagio4 = false;

    private Boolean flagContagemPicos = false;
    private int qtdTotalPicos = 0;
    private final int QTD_MINIMA_PICOS_DESMAIO = 4;
    
    long timestampEstagio1 = 0;
	long timestampEstagio2 = 0;
	long timestampEstagio3 = 0;
	long timestampEstagio4 = 0;
	long intervaloEstagio1e2 = 0;
	long intervaloEstagio2e3 = 0;
	long intervaloEstagio1e3 = 0;
	
	private final double ACELERACAO_NORMAL_GRAVIDADE = 9.8;
	double moduloAceleracaoEstagio1 = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade... 
	double moduloAceleracaoEstagio2 = 0;
	
	private final double LIMITE_PICO_INFERIOR = 8;
	private final double LIMITE_PICO_SUPERIOR = 11;
	private final int MARGEM_ERRO_TEMPO_ACELERACAO_DESACELERACAO = 200; // EM MILISEGUNDOS...
	private final int MARGEM_ERRO_TEMPO_TOTAL_QUEDA_SINAL_ESTABILIZADO = 450; // EM MILISEGUNDOS...
	private final int MARGEN_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO = 2000;
	private final int MARGEN_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO = 6000;
	private final double MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO = 0.5;
	private final double MARGEM_ERRO_AMPLITUDE_ACELERACAO = 8;
	private final int QTD_TOTAL_AMOSTRAGEM_ACELERACAO = 45;
	Stack<Double> arrayAmostragemAceleracao = new Stack<Double>();

	private final int MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO = 5;
	private final double MARGEM_ERRO_ACELERACAO_DESMAIO = 0.5;
	int contadoMargemErroDesmaio = 0;
	
	private final int ID_EIXO_X = 1;
	private final int ID_EIXO_Y = 2;
	private final int ID_EIXO_Z = 3;
	
	private final int ID_EIXO_X_POSITIVO =  1 * ID_EIXO_X;
	private final int ID_EIXO_X_NEGATIVO = -1 * ID_EIXO_X;
	private final int ID_EIXO_Y_POSITIVO =  1 * ID_EIXO_Y;
	private final int ID_EIXO_Y_NEGATIVO = -1 * ID_EIXO_Y;
	private final int ID_EIXO_Z_POSITIVO =  1 * ID_EIXO_Z;
	private final int ID_EIXO_Z_NEGATIVO = -1 * ID_EIXO_Z;
	private final int QTD_TOTAL_AMOSTRAGEM_EIXO_ACELERACAO = 40;
	Stack<Float> eixoNormalAceleracaoAntesX = new Stack<Float>();
	Stack<Float> eixoNormalAceleracaoAntesY = new Stack<Float>();
	Stack<Float> eixoNormalAceleracaoAntesZ = new Stack<Float>();

	Stack<Float> eixoNormalAceleracaoDepoisX = new Stack<Float>();
	Stack<Float> eixoNormalAceleracaoDepoisY = new Stack<Float>();
	Stack<Float> eixoNormalAceleracaoDepoisZ = new Stack<Float>();

	private final double MARGEM_ERRO_MINIMO_GYROSCOPE = 4;
	private double maxVariacaoGyroscopeEixoX = 0;
	private double maxVariacaoGyroscopeEixoY = 0;
	private double maxVariacaoGyroscopeEixoZ = 0;
	
	String chaveNomeArquivoLog = "";
     
    Float x, y, z;
    Float xGiroscopio, yGiroscopio, zGiroscopio;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    
    private String TAG = "logs";
    
	// Intervalo em milisegundos
	private final int INTERVAL = 1000;
	
	// Duração em milisegundos
	private final int DURATION = 60000;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
         
        textViewTimer = (TextView) findViewById(R.id.txtValorTimer);
        textViewX = (TextView) findViewById(R.id.txtValorX);
        textViewY = (TextView) findViewById(R.id.txtValorY);
        textViewZ = (TextView) findViewById(R.id.txtValorZ);
        textViewXG = (TextView) findViewById(R.id.txtValorXG);
        textViewYG = (TextView) findViewById(R.id.txtValorYG);
        textViewZG = (TextView) findViewById(R.id.txtValorZG);
        textViewVetor = (TextView) findViewById(R.id.txtValorVetor);
        textViewDetail = (TextView) findViewById(R.id.text_view_detail);
        textViewDesmaio = (TextView) findViewById(R.id.text_view_desmaio);
        textViewStatus = (TextView) findViewById(R.id.text_view_status);

        /**TODO: RAWLINSON - NAO PRECISA DESSE BOTAO...
        Button btnCriarArquivo = (Button) findViewById(R.id.btnGerarArquivo);
        btnCriarArquivo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CriaArquivosLog(x, y, z);
			}
		});
		**/
        
        /********************************************************************************
         *						HEURISTICA DE DETECCAO DE DESMAIO						*
         ********************************************************************************/
        flagGyroscopeAtivado = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        chaveNomeArquivoLog = getChaveArquivoLog();
        
        // Obtendo instante inicial do log...
        miliTimeInicial = System.currentTimeMillis();
    }
       
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);
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
    public void onSensorChanged(SensorEvent event)
    {
    	x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        switch (event.sensor.getType()) {
	    	case Sensor.TYPE_ACCELEROMETER:
	            textViewX.setText("Posicao AX: " + x.intValue() + " Float: " + x);
	            textViewY.setText("Posicao AY: " + y.intValue() + " Float: " + y);
	            textViewZ.setText("Posicao AZ: " + z.intValue() + " Float: " + z);
	        	
	    		// Instante de tempo que o log foi gerado...
	            long timestampAtualSistema = System.currentTimeMillis();
	    		double miliTimeAtual = (timestampAtualSistema - miliTimeInicial) / 1000.0;
	    		
	    		// Calculando os modulos resultantes dos eixos x, y e z
	    		double moduloVetorAceleracao = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	            
	        	// Atualizando array de amostragens da aceleracao...
	    		if(arrayAmostragemAceleracao.size() >= QTD_TOTAL_AMOSTRAGEM_ACELERACAO)
	    		{
	    			arrayAmostragemAceleracao.pop();
	    		}
	    		arrayAmostragemAceleracao.add(0, moduloVetorAceleracao);
	    		
	    		/**************** BEGIN: COLETANDO DADOS DOS EIXOS... BASEADO NO HISTORICO PASSADO... ********************/
	    		if(flagEstagio1 == false && flagEstagio2 == false && flagEstagio3 == false && flagEstagio4 == false)
	    		{
	    			int qtdAmostragemEixo = eixoNormalAceleracaoAntesX.size();
	    			if(qtdAmostragemEixo >= QTD_TOTAL_AMOSTRAGEM_EIXO_ACELERACAO)
	    			{
	    				eixoNormalAceleracaoAntesX.pop();
	    				eixoNormalAceleracaoAntesY.pop();
	    				eixoNormalAceleracaoAntesZ.pop();
	    			}
	    			eixoNormalAceleracaoAntesX.add(0, x);
	    			eixoNormalAceleracaoAntesY.add(0, y);
	    			eixoNormalAceleracaoAntesZ.add(0, z);
	    		}
	    		else
	    		{
	    			int qtdAmostragemEixo = eixoNormalAceleracaoDepoisX.size();
	    			if(qtdAmostragemEixo >= QTD_TOTAL_AMOSTRAGEM_EIXO_ACELERACAO)
	    			{
	    				eixoNormalAceleracaoDepoisX.pop();
	    				eixoNormalAceleracaoDepoisY.pop();
	    				eixoNormalAceleracaoDepoisZ.pop();
	    			}
	    			eixoNormalAceleracaoDepoisX.add(0, x);
	    			eixoNormalAceleracaoDepoisY.add(0, y);
	    			eixoNormalAceleracaoDepoisZ.add(0, z);
	    		}
	    		/**************** END: COLETANDO DADOS DOS EIXOS... BASEADO NO HISTORICO PASSADO... ********************/
	    		

	        	textViewTimer.setText("Timer: " + miliTimeAtual);
	            textViewVetor.setText("Vetor Aceleracao: " + Double.toString(moduloVetorAceleracao));
            	textViewDetail.setText(
    	    	        "\nEixo Normal Antes: " + obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ) + 
    	    	        "\nEixo Normal Depois: " + obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ));
 	             
	            // O codigo abaixo mostra a direcao na qual o celular esta dentro do plano cartesiano...
//	            if(y < 0) { // O dispositivo esta de cabeca pra baixo
//	                if(x > 0)  
//	                    textViewDetail.setText("Virando para ESQUERDA ficando INVERTIDO");
//	                if(x < 0)  
//	                    textViewDetail.setText("Virando para DIREITA ficando INVERTIDO");   
//	            } else {
//	                if(x > 0)  
//	                    textViewDetail.setText("Virando para ESQUERDA ");
//	                if(x < 0)  
//	                    textViewDetail.setText("Virando para DIREITA ");
//	            }
	            
	            /********************************************************************************
	             *						HEURISTICA DE DETECCAO DE DESMAIO						*
	             ********************************************************************************/
	            if(flagEstagio4 == true)
	            {
	            	double tempoTotalValidacaoDesmaio = timestampAtualSistema - timestampEstagio4;
	            	
	            	textViewDesmaio.setText("Picos Ultimo Desmaio..." + qtdTotalPicos + " Eixo Normal Antes: " + obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ) + 
    	    	        "\nEixo Normal Depois: " + obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ));

	            	if(tempoTotalValidacaoDesmaio > MARGEN_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO)
	            	{
	    	        	if(tempoTotalValidacaoDesmaio < MARGEN_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO)
	    	        	{
	    	        		// Verificar se houve alguma oscilação no acelerometro... caso contrario o cara apagou mesmo... :P
	    	        		double mediaAmostralAceleracao = Math.abs(obterMediaVariacaoAceleracao());
	    	    	        if(mediaAmostralAceleracao > MARGEM_ERRO_ACELERACAO_DESMAIO)
	    	        		{
	    	    	        	contadoMargemErroDesmaio++;
	    	        		}
	    	    	        
	    	    	        if(contadoMargemErroDesmaio > MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO)
	    	    	        {
	    	    	        	textViewStatus.setText(textViewStatus.getText() + "\n - DESMAIO CANCELADO!!!! A pessoal se mexeu...");
	    	
	    	    	        	textViewStatus.setText(
	    	    	        	textViewStatus.getText() + 
	    	    				"\n" + maxVariacaoGyroscopeEixoX + 
	    	    				"\n" + maxVariacaoGyroscopeEixoY + 
	    	    				"\n" + maxVariacaoGyroscopeEixoZ + 
	    	    				"\n" + intervaloEstagio1e2 + 
	    	    				"\n" + intervaloEstagio2e3 + 
	    	    				"\n" + intervaloEstagio1e3 + 
	    	    				"\n" + moduloAceleracaoEstagio1 + 
	    	    				"\n" + moduloAceleracaoEstagio2 
	    	    	        	);
	    	    	        	
	    	    	        	resetarVariaveisMonitoramento();
	    	    	        }
	    	        	}
	    	        	else
	    	        	{
	    	        		int eixoNormalAntes = obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ);
	    	        		int eixoNormalDepois = obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ);
	    	        		
	    	        		// A condicao abaixo verifica se a pessoa estava de pé e deitou ou virou... ou seja, a pessoa não está na mesma posicao antes do impacto.
	    	        		if(eixoNormalAntes != eixoNormalDepois && Math.abs(eixoNormalAntes) != Math.abs(eixoNormalDepois))
	    	        		{
	    	        			Boolean flagAtivarAlarteDesmaio = true;
	    	        			
	    	        			// Verificando se houve alguma variacao angular no giroscopio...
	    	        			if(flagGyroscopeAtivado)
	    	        			{
    	        					switch (Math.abs(eixoNormalAntes)) {
    	        						case ID_EIXO_X:
    	        							if(maxVariacaoGyroscopeEixoY > MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ > MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlarteDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlarteDesmaio = false;
    	        							}
	    	        			    	break;
    	        						case ID_EIXO_Y:
    	        							if(maxVariacaoGyroscopeEixoX > MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ > MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlarteDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlarteDesmaio = false;
    	        							}
	    	        			    	break;
    	        						case ID_EIXO_Z:
    	        							if(maxVariacaoGyroscopeEixoX > MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoY > MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlarteDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlarteDesmaio = false;
    	        							}
	    	        			    	break;
    	        					}
	    	        			}
	    	        			
	    	        			if(flagAtivarAlarteDesmaio)
	    	        			{
		    		        		textViewStatus.setText(textViewStatus.getText() + "\n - DESMAIO DETECTADO");
		    		
		    		            	textViewStatus.setText(
		    		            	textViewStatus.getText() + 
		    	    				"\n" + maxVariacaoGyroscopeEixoX + 
		    	    				"\n" + maxVariacaoGyroscopeEixoY + 
		    	    				"\n" + maxVariacaoGyroscopeEixoZ + 
		    		    			"\n" + intervaloEstagio1e2 + 
		    		    			"\n" + intervaloEstagio2e3 + 
		    		    			"\n" + intervaloEstagio1e3 + 
		    		    			"\n" + moduloAceleracaoEstagio1 + 
		    		    			"\n" + moduloAceleracaoEstagio2
		    		            	);
		    		        		
		    		        		// Exibindo alerta de desmaio...
		    		        		showDialogDesmaio();
		    			        	
		    			        	resetarVariaveisMonitoramento();
	    	        			}
	    	        			else
	    	        			{
		    	    	        	textViewStatus.setText(textViewStatus.getText() + "\n - DESMAIO CANCELADO!!!! A pessoa nao caiu no chao... :P");
		    	    		    	
		    	    	        	textViewStatus.setText(
		    	    	        	textViewStatus.getText() + 
		    	    				"\n" + maxVariacaoGyroscopeEixoX + 
		    	    				"\n" + maxVariacaoGyroscopeEixoY + 
		    	    				"\n" + maxVariacaoGyroscopeEixoZ + 
		    	    				"\n" + intervaloEstagio1e2 + 
		    	    				"\n" + intervaloEstagio2e3 + 
		    	    				"\n" + intervaloEstagio1e3 + 
		    	    				"\n" + moduloAceleracaoEstagio1 + 
		    	    				"\n" + moduloAceleracaoEstagio2 
		    	    	        	);
		    	    	        	
		    	    	        	resetarVariaveisMonitoramento();
	    	        			}
	    	        		}
	    	        		else
	    	        		{
	    	    	        	textViewStatus.setText(textViewStatus.getText() + "\n - DESMAIO CANCELADO!!!! A pessoa esta na mesma posicao...");
	    	
	    	    	        	textViewStatus.setText(
	    	    	        	textViewStatus.getText() + 
	    	    				"\n" + maxVariacaoGyroscopeEixoX + 
	    	    				"\n" + maxVariacaoGyroscopeEixoY + 
	    	    				"\n" + maxVariacaoGyroscopeEixoZ + 
	    	    				"\n" + intervaloEstagio1e2 + 
	    	    				"\n" + intervaloEstagio2e3 + 
	    	    				"\n" + intervaloEstagio1e3 + 
	    	    				"\n" + moduloAceleracaoEstagio1 + 
	    	    				"\n" + moduloAceleracaoEstagio2 
	    	    	        	);
	    	    	        	
	    	    	        	resetarVariaveisMonitoramento();
	    	        		}
	    	        	}
	            	}
	            	else
	            	{
	            		contadoMargemErroDesmaio = 0;
	            	}
	            }
	            else
	            {
	            	if(moduloVetorAceleracao <= LIMITE_PICO_INFERIOR)
	                {
	            		if(flagContagemPicos == false)
	            		{
	            			qtdTotalPicos += 1;
	            			flagContagemPicos = true;
	            		}
	            		
	            		if(moduloVetorAceleracao < moduloAceleracaoEstagio1)
	            		{
	    	                // Obter tempo em milisegundos... para estimar com mais precisao o processo de queda...
	    	                if(flagEstagio1 == false)
	    	                {
	    	            		timestampEstagio1 = timestampAtualSistema; // Nao deve ser inicializado o tempo inicial do estagio 1 ao identificar picos menores...
	    	                }
	    	
	    	                flagEstagio1 = true;
	    	                moduloAceleracaoEstagio1 = moduloVetorAceleracao;
	    	                
	    	                textViewStatus.setText("\n Novo Desmaio... \n - ESTAGIO_1");
	            		}
	                }
	         		
	                if(flagEstagio1 == true)
	                {
	                	// Verificar se o sinal do acelerometro estabilizou... atraves de uma margem de erro em relacao a normal 9,8 
	        	        double mediaAmostralAceleracao = Math.abs(obterMediaVariacaoAceleracao());
	        	        if(mediaAmostralAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO && flagEstagio2 == false) // Verificar se o sinal do acelerometro estabilizou...
	        	        {
	        	        	resetarVariaveisMonitoramento();
	        	        }

	        	        if(moduloVetorAceleracao >= LIMITE_PICO_SUPERIOR)
	                	{
	        	        	flagContagemPicos = false;    	        	
	        	        	if(moduloVetorAceleracao >= moduloAceleracaoEstagio2)
	        	        	{
	    	                	flagEstagio2 = true;
	    	            		timestampEstagio2 = timestampAtualSistema;
	    	            		intervaloEstagio1e2 = timestampEstagio2 - timestampEstagio1;
	    	
	    	            		moduloAceleracaoEstagio2 = moduloVetorAceleracao;
	    	                    
	    	            		textViewStatus.setText(textViewStatus.getText() + "\n - ESTAGIO_2");
	        	        	}
	                	}
	                }

	                if(flagEstagio2 == true)
	                {
	                	// Verificar se o cara apagou... atraves de uma margem de erro em relacao a normal 9,8 
	        	        double mediaAmostralAceleracao = Math.abs(obterMediaVariacaoAceleracao());
	        	        if(mediaAmostralAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
	        	        {
	        	        	if(flagEstagio3 == false)
	        	        	{
	        	        		textViewStatus.setText(textViewStatus.getText() + "\n - ESTAGIO_3");
	        	        	}
	        	        	
	        	        	flagEstagio3 = true;
	        	        	timestampEstagio3 = timestampAtualSistema;
	        	        	intervaloEstagio2e3 = timestampEstagio3 - timestampEstagio2;
	        	        	intervaloEstagio1e3 = timestampEstagio3 - timestampEstagio1;
	        	        }
	                }
	                
	                if(flagEstagio3 == true)
	                {
	                	// Obtendo amplitude de aceleracao entre o menor pico e o maior pico...
	                	double amplitudeAceleracao = moduloAceleracaoEstagio2 - moduloAceleracaoEstagio1;
	                	
	                	if(amplitudeAceleracao > MARGEM_ERRO_AMPLITUDE_ACELERACAO)
	                	{
	                		if(intervaloEstagio1e2 >= MARGEM_ERRO_TEMPO_ACELERACAO_DESACELERACAO && intervaloEstagio1e3 >= MARGEM_ERRO_TEMPO_TOTAL_QUEDA_SINAL_ESTABILIZADO && qtdTotalPicos >= QTD_MINIMA_PICOS_DESMAIO)
	                		{
	    	            		flagEstagio4 = true;
	    	            		timestampEstagio4 = timestampAtualSistema;
	    	                    
	    	                    textViewStatus.setText(textViewStatus.getText() + "\n - VALIDANDO POSSIVEL DESMAIO");
	                		}
	                    	else
	                    	{
	                    		resetarVariaveisMonitoramento();
	                    	}
	                	}
	                }
	            }

	            // Gerando os logs do sistema...
	            //TODO: CriaArquivosLog(miliTimeAtual, x, y, z, moduloVetorAceleracao);
	    	break;
	    	
	    	case Sensor.TYPE_GYROSCOPE:
	    		flagGyroscopeAtivado = true;
	    		
	    		if(flagEstagio1 == true || flagEstagio2 == true || flagEstagio3 == true || flagEstagio4 == true)
	    		{
	    			double moduloX = Math.abs(x);
	    			double moduloY = Math.abs(y);
	    			double moduloZ = Math.abs(z);
	    			
	    			if(moduloX > maxVariacaoGyroscopeEixoX)
	    			{
	    				maxVariacaoGyroscopeEixoX = moduloX;
	    			}
	    			
	    			if(moduloY > maxVariacaoGyroscopeEixoY)
	    			{
	    				maxVariacaoGyroscopeEixoY = moduloY;
	    			}
	    			
	    			if(moduloZ > maxVariacaoGyroscopeEixoZ)
	    			{
	    				maxVariacaoGyroscopeEixoZ = moduloZ;
	    			}
	    		}
    			
	            textViewXG.setText("Posicao GX: " + x.intValue() + " Float: " + x);
	            textViewYG.setText("Posicao GY: " + y.intValue() + " Float: " + y);
	            textViewZG.setText("Posicao GZ: " + z.intValue() + " Float: " + z);
	    	break;
    	}
    }
    
    // Esta funcao retorna a porcentagem media de variacao da aceleracao... Ex.: -0.21, 0.50, 0.01, etc
    public void resetarVariaveisMonitoramento() {
    	timestampEstagio1 = 0;
    	timestampEstagio2 = 0;
    	timestampEstagio3 = 0;
    	timestampEstagio4 = 0;
    	intervaloEstagio1e2 = 0;
    	intervaloEstagio2e3 = 0;
    	intervaloEstagio1e3 = 0;
    	moduloAceleracaoEstagio1 = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade.
    	moduloAceleracaoEstagio2 = 0;
    	flagEstagio1 = false;
    	flagEstagio2 = false;
    	flagEstagio3 = false;
    	flagEstagio4 = false;
    	
    	flagContagemPicos = false;
    	qtdTotalPicos = 0;
    	
    	contadoMargemErroDesmaio = 0;

    	eixoNormalAceleracaoAntesX.clear();
    	eixoNormalAceleracaoAntesY.clear();
    	eixoNormalAceleracaoAntesZ.clear();
    	
    	eixoNormalAceleracaoDepoisX.clear();
    	eixoNormalAceleracaoDepoisY.clear();
    	eixoNormalAceleracaoDepoisZ.clear();
    	
    	flagGyroscopeAtivado = false;
    	maxVariacaoGyroscopeEixoX = 0;
    	maxVariacaoGyroscopeEixoY = 0;
    	maxVariacaoGyroscopeEixoZ = 0;
    }
    
    // Esta funcao retorna a porcentagem media de variacao da aceleracao... Ex.: -0.21, 0.50, 0.01, etc
    public double obterMediaVariacaoAceleracao() {
    	double totalVariacaoAceleracao = 0;
    	double mediaVariacaoAceleracao = 0;
    	
    	for(Double valorAceleracao : arrayAmostragemAceleracao) {
    		totalVariacaoAceleracao += valorAceleracao;
    	}
    	
    	mediaVariacaoAceleracao = ACELERACAO_NORMAL_GRAVIDADE - (totalVariacaoAceleracao / QTD_TOTAL_AMOSTRAGEM_ACELERACAO);
    	
    	return(mediaVariacaoAceleracao);
    }
    
    // A funcao retorna o eixo normal do celular, atraves de comparacoes entre os eixos X, Y e Z
    public int obterEixoNormal(Stack<Float> eixoX, Stack<Float> eixoY, Stack<Float> eixoZ) {
    	double totalAceleracaoEixoX_Positivo = 0;
    	double totalAceleracaoEixoX_Negativo = 0;
    	double totalAceleracaoEixoY_Positivo = 0;
    	double totalAceleracaoEixoY_Negativo = 0;
    	double totalAceleracaoEixoZ_Positivo = 0;
    	double totalAceleracaoEixoZ_Negativo = 0;

    	double totalAceleracaoEixoX = 0;
    	double totalAceleracaoEixoY = 0;
    	double totalAceleracaoEixoZ = 0;
    	
    	for(Float valorAceleracao : eixoX) {
    		if(valorAceleracao >= 0)
    		{
    			totalAceleracaoEixoX_Positivo += Math.abs(valorAceleracao);
    		}
    		else
    		{
    			totalAceleracaoEixoX_Negativo += Math.abs(valorAceleracao);
    		}
    	}
    	for(Float valorAceleracao : eixoY) {
    		if(valorAceleracao >= 0)
    		{
    			totalAceleracaoEixoY_Positivo += Math.abs(valorAceleracao);
    		}
    		else
    		{
    			totalAceleracaoEixoY_Negativo += Math.abs(valorAceleracao);
    		}
        }
    	for(Float valorAceleracao : eixoZ) {
    		if(valorAceleracao >= 0)
    		{
    			totalAceleracaoEixoZ_Positivo += Math.abs(valorAceleracao);
    		}
    		else
    		{
    			totalAceleracaoEixoZ_Negativo += Math.abs(valorAceleracao);
    		}
        }

    	
    	totalAceleracaoEixoX = totalAceleracaoEixoX_Positivo + totalAceleracaoEixoX_Negativo;
    	totalAceleracaoEixoY = totalAceleracaoEixoY_Positivo + totalAceleracaoEixoY_Negativo;
    	totalAceleracaoEixoZ = totalAceleracaoEixoZ_Positivo + totalAceleracaoEixoZ_Negativo;
    	
    	if(totalAceleracaoEixoX > totalAceleracaoEixoY)
    	{
    		if(totalAceleracaoEixoX > totalAceleracaoEixoZ)
    		{
    			if(totalAceleracaoEixoX_Positivo > totalAceleracaoEixoX_Negativo)
    				return(ID_EIXO_X_POSITIVO);
    			else
    				return(ID_EIXO_X_NEGATIVO);
    		}
    		else
    		{
    			if(totalAceleracaoEixoZ_Positivo > totalAceleracaoEixoZ_Negativo)
    				return(ID_EIXO_Z_POSITIVO);
    			else
    				return(ID_EIXO_Z_NEGATIVO);
    		}
    	}
    	else
    	{
    		if(totalAceleracaoEixoY > totalAceleracaoEixoZ)
    		{
    			if(totalAceleracaoEixoY_Positivo > totalAceleracaoEixoY_Negativo)
    				return(ID_EIXO_Y_POSITIVO);
    			else
    				return(ID_EIXO_Y_NEGATIVO);
    		}
    		else
    		{
    			if(totalAceleracaoEixoZ_Positivo > totalAceleracaoEixoZ_Negativo)
    				return(ID_EIXO_Z_POSITIVO);
    			else
    				return(ID_EIXO_Z_NEGATIVO);
    		}
    	}
    }

    public void CriaArquivosLog(Double miliTimeAtual, Float x, Float y, Float z, Double moduloVetorAceleracao)
    {
    	try
    	{
//    		/** BEGIN: Salvando os dados do acelerometro em um log dentro do CARD SD... **/
//    		File arq = new File(Environment.getExternalStorageDirectory(), "logsDadosAcelerometro_" + chaveNomeArquivoLog + ".txt");
//			FileOutputStream escrever = new FileOutputStream(arq, true);
//			
//			escrever.write(x.toString().getBytes());
//			escrever.write(";".getBytes());
//			escrever.write(y.toString().getBytes());
//			escrever.write(";".getBytes());
//			escrever.write(z.toString().getBytes());
//			escrever.write("\n".getBytes());
//			escrever.flush();
//			escrever.close();
//    		/** END: Salvando os dados do acelerometro em um log dentro do CARD SD... **/
			
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		File arqLog = new File(Environment.getExternalStorageDirectory(), "logGraficoModuloAceleracao_" + chaveNomeArquivoLog + ".txt");
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

	public void showDialogDesmaio()
	{
        // Prepara o Dialog informando o título, mensagem e cria o Positive Button        
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.title);
        alertBuilder.setMessage("Um possível demaio foi detectado! Enviando SMS ao fim da contagem...");
        
        // Aqui você pode definir a ação de clique do botão
        alertBuilder.setPositiveButton("OK", null);
                
        // Criar o AlertDialog
        final AlertDialog alert = alertBuilder.create();
        
        alert.show();

        // Iniciando objetos de musica do android...
    	objNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        objRing = RingtoneManager.getRingtone(getApplicationContext(), objNotification);

        // Pega o botão do Alert
		btnAlertDesmaio = alert.getButton(AlertDialog.BUTTON_POSITIVE);
		
		// Objeto vibracao...
		final Vibrator objVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// Start without a delay
		// Each element then alternates between vibrate, sleep, vibrate, sleep...
		long[] pattern = {300, 100, 1000, 300, 200, 100, 500, 200, 100};
		objVibrator.vibrate(pattern, 0);
		
        // Cria um objeto da classe CountDownTimer 
        // informando a duração e o intervalo
        final CountDownTimer timerDialog = new CountDownTimer(DURATION, INTERVAL) {

			// A cada 1 segundo o onTick é invocado
        	@Override
			public void onTick(long millisUntilFinished) {
 				// Formata o texto do botão com os segundos. 
				// Ex. OK (9)
        		btnAlertDesmaio.setText(("Cancelar envio de SMS? ("+(millisUntilFinished/INTERVAL)) + ")");

		        // Toca o alerta sonoro...
		        try {
		        	if(!objRing.isPlaying())
		        	{
		        		objRing.play();
		        	}
		        } catch (Exception e) {}
			}
			
			@Override
			public void onFinish() {
				// Fecha o alert
				alert.dismiss();
			}
		}.start();
        
		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(final DialogInterface dialog) {
				// Para o alerta sonoro...
		        try {
		        	timerDialog.cancel(); // Para o contador do alerte...
		        	
		        	objVibrator.cancel(); // Faz o celular parar de vibrar...
		        	
		        	objRing.stop(); // Para de tocar a musica...
		        } catch (Exception e) {}
			}
		});
	}
	
	public static String getChaveArquivoLog() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}
}