package com.promobile.epilepticdetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;

import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AutomatedTestHeuristicActivity extends Activity{
	String chaveNomeArquivoLog = "";
	private final String CATACTER_SEPARADOR_ARQ_LOGS = " - ";

	static String TAG = "AutomatedTestHeuristicActivity";
    
    // O path abaixo contem a lista de arquivos de teste automatizados...
    String pathArquivosTeste = "";
    String pathArquivoLogResultado = "";
	
	String chaveTeste = "";
	String pathDadosTesteAcelerometro = "";
	String pathDadosTesteGiroscopio = "";
	
	Handler handler;
	
    private Context context;
    private Button btnStartTest;
    private TextView txtChaveTeste;
    private TextView textViewTimer;
	private TextView textViewXA;
    private TextView textViewYA;
    private TextView textViewZA;
	private TextView textViewXG;
    private TextView textViewYG;
    private TextView textViewZG;
    private TextView textViewVetor;
    private TextView textViewStatus;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_automated_test_heuristic);

        context = getApplicationContext();
         
        txtChaveTeste = (TextView) findViewById(R.id.txtChaveTeste);
        textViewTimer = (TextView) findViewById(R.id.txtValorTimer);
        textViewXA = (TextView) findViewById(R.id.txtValorX);
        textViewYA = (TextView) findViewById(R.id.txtValorY);
        textViewZA = (TextView) findViewById(R.id.txtValorZ);
        textViewXG = (TextView) findViewById(R.id.txtValorXG);
        textViewYG = (TextView) findViewById(R.id.txtValorYG);
        textViewZG = (TextView) findViewById(R.id.txtValorZG);
        textViewVetor = (TextView) findViewById(R.id.txtValorVetor);
        textViewStatus = (TextView) findViewById(R.id.text_view_status);
        textViewStatus.setMovementMethod(new ScrollingMovementMethod());
        
        btnStartTest = (Button) findViewById(R.id.btnStartTest);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	chaveNomeArquivoLog = getChaveArquivoLog();
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                	pathArquivosTeste = Environment.getExternalStorageDirectory().toString() + "/AutomatedTestHeuristic/";
                	pathArquivoLogResultado = Environment.getExternalStorageDirectory().toString() + "/AutomatedTestHeuristic/LOG_RESULTADO/";
                    
                    startAutomatedTest();
                }
                else {
                	Toast.makeText(context, "EpilepsyApp - Não foi possível encontrar o diretório de teste!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler();
        txtChaveTeste.setText("");
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
     
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    protected void startAutomatedTest() {
    	textViewStatus.setText("Processando os testes automatizados... Aguarde...\n");

		File diretorio = new File(pathArquivosTeste);
        if (!diretorio.exists()) {  
        	diretorio.mkdirs(); //mkdir() cria somente um diretório, mkdirs() cria diretórios e subdiretórios.  
        } 

        File[] arquivos = diretorio.listFiles();
    	
    	if(arquivos != null)
    	{
    		int length = arquivos.length;
    	    for(int i = 0; i < length; ++i)
    	    {
    	        File f = arquivos[i];
    	        if(f.isFile() && f.getName().contains("logsEpilepsyApp_Accelerometer_"))
    	        {
    	        	int contA = 0;
    	        	Stack<Double> timerA = new Stack<Double>();
    	        	Stack<Double> eixoXA = new Stack<Double>();
    	        	Stack<Double> eixoYA = new Stack<Double>();
    	        	Stack<Double> eixoZA = new Stack<Double>();
    	        	Stack<Double> eixoMA = new Stack<Double>();

    	        	int contG = 0;
    	        	Stack<Double> timerG = new Stack<Double>();
    	        	Stack<Double> eixoXG = new Stack<Double>();
    	        	Stack<Double> eixoYG = new Stack<Double>();
    	        	Stack<Double> eixoZG = new Stack<Double>();
    	        	Stack<Double> eixoMG = new Stack<Double>();

    	        	chaveTeste = f.getName().replace("logsEpilepsyApp_Accelerometer_", "").replace(".txt", "");
    	        	pathDadosTesteAcelerometro =  "logsEpilepsyApp_Accelerometer_" + chaveTeste + ".txt";
    	        	pathDadosTesteGiroscopio = "logsEpilepsyApp_Gyroscope_" + chaveTeste + ".txt";

    	        	try
    	            {
	    	        	File arqDadosTeste;
	    	        	Scanner scanner;
	    	        	
	    	        	arqDadosTeste = new File(pathArquivosTeste, pathDadosTesteAcelerometro);
	    	        	scanner = new Scanner(arqDadosTeste);
	    	            while (scanner.hasNextLine()) {
	    	                String[] arrayDados = scanner.nextLine().split(";");
							timerA.add(contA, Double.parseDouble(arrayDados[0]));
							eixoXA.add(contA, Double.parseDouble(arrayDados[1]));
							eixoYA.add(contA, Double.parseDouble(arrayDados[2]));
							eixoZA.add(contA, Double.parseDouble(arrayDados[3]));
							eixoMA.add(contA, Double.parseDouble(arrayDados[4]));
							contA = contA + 1;
						}
	    	            
	    	        	arqDadosTeste = new File(pathArquivosTeste, pathDadosTesteGiroscopio);
	    	        	scanner = new Scanner(arqDadosTeste);
	    	            while (scanner.hasNextLine()) {
	    	                String[] arrayDados = scanner.nextLine().split(";");
							timerG.add(contG, Double.parseDouble(arrayDados[0]));
							eixoXG.add(contG, Double.parseDouble(arrayDados[1]));
							eixoYG.add(contG, Double.parseDouble(arrayDados[2]));
							eixoZG.add(contG, Double.parseDouble(arrayDados[3]));
							eixoMG.add(contG, Double.parseDouble(arrayDados[4]));
							contG = contG + 1;
						}
	    	            scanner.close();
    	            }
    	            catch (Exception e)
    	            {
    	            	textViewStatus.append("# " + chaveTeste + " - Erro durante a leitura!\n");
    	            	salvarLog(chaveTeste, "Erro durante a leitura dos dados do arquivo!");
    	            }
    	        	
    	        	if(timerA.size() > 0 && timerG.size() > 0)
    	        	{
    	        		processarMonitoramento(chaveTeste, timerA, eixoXA, eixoYA, eixoZA, eixoMA, timerG, eixoXG, eixoYG, eixoZG, eixoMG);
    	        	}
    	        	else
    	        	{
    	        		textViewStatus.append("# " + chaveTeste + " - Um dos arquivos não possui dados!\n");
    	        		salvarLog(chaveTeste, "Um dos arquivos não possui dados!");
    	        	}
    	        }
    	    }
    	}
    }

    private void processarMonitoramento(String strChaveTeste, Stack<Double> timerA, Stack<Double> eixoXA, Stack<Double> eixoYA, Stack<Double> eixoZA, Stack<Double> eixoMA, Stack<Double> timerG, Stack<Double> eixoXG, Stack<Double> eixoYG, Stack<Double> eixoZG, Stack<Double> eixoMG)
    {
    	final String chaveTeste = strChaveTeste;
    	final Stack<Double> timer_A = timerA;
    	final Stack<Double> eixoX_A = eixoXA;
    	final Stack<Double> eixoY_A = eixoYA;
    	final Stack<Double> eixoZ_A = eixoZA;
    	final Stack<Double> eixoM_A = eixoZA;

    	final Stack<Double> timer_G = timerG;
    	final Stack<Double> eixoX_G = eixoXG;
    	final Stack<Double> eixoY_G = eixoYG;
    	final Stack<Double> eixoZ_G = eixoYG;
    	final Stack<Double> eixoM_G = eixoMG;

    	Runnable runnable = new Runnable() {
    	    private EpilepsyHeuristic objHeuristica;
    	    int contA;
    		int contG;
    		long miliTimeInicial;
    		boolean flagMonitoramento;
    		boolean flagFimDadosAcelerometro;
    		boolean flagFimDadosGiroscopio;
    		double timerAtualA;
    		double timerAtualG;
    		boolean flagConstrutor = true;
    		
    		public void run() 
    	    {
    			if(flagConstrutor) // Guambi para criar um contrutor... heheheh :-P
    			{
    				if(txtChaveTeste.getText().length() == 0 || txtChaveTeste.getText().equals(chaveTeste))
    				{
    					txtChaveTeste.setText(chaveTeste);
    				}
    				else
    				{
    					handler.postDelayed(this, 100);
    					return;
    				}
    				
    				objHeuristica = new EpilepsyHeuristic(context, EpilepsyHeuristic.PERFIL_PRECISAO, false);
    				timerAtualA = 0;
    				timerAtualG = 0;
		    	    contA = 0;
		    		contG = 0;
		    		miliTimeInicial = System.currentTimeMillis();
		    		flagMonitoramento = false;
		    		flagFimDadosAcelerometro = false;
		    		flagFimDadosGiroscopio = false;
		    		flagConstrutor = false;
    			}
    			
    	    	long timestampAtualSistema = System.currentTimeMillis();
    	    	double miliTimeAtual = (timestampAtualSistema - miliTimeInicial) / 1000.0;
    	    	boolean flagSincronizarDados = true;
				
    	    	while(flagSincronizarDados)
    	    	{
					timerAtualA = timer_A.get(contA);
					timerAtualG = timer_G.get(contG);
	
	    			if(!flagFimDadosAcelerometro && timerAtualA <= miliTimeAtual)
	    			{
	    				// Verificando se esta acontAecendo algum desmaio ou ataque epileptico...
	        			//Log.i(TAG, "A#" + timerA.get(contA) + "|" + eixoXA.get(contA) + "|" + eixoYA.get(contA) + "|" + eixoZA.get(contA));
	    				textViewTimer.setText("Timer: " + timerAtualA);
	    				textViewXA.setText("Posicao AX: " + eixoX_A.get(contA).intValue() + " Float: " + eixoX_A.get(contA));
	    	            textViewYA.setText("Posicao AY: " + eixoY_A.get(contA).intValue() + " Float: " + eixoY_A.get(contA));
	    	            textViewZA.setText("Posicao AZ: " + eixoZ_A.get(contA).intValue() + " Float: " + eixoZ_A.get(contA));
	    	            textViewVetor.setText("Vetor Aceleracao: " + Double.toString(eixoM_A.get(contA)));
	        	    	if(objHeuristica.monitorar(eixoX_A.get(contA), eixoY_A.get(contA), eixoZ_A.get(contA), Sensor.TYPE_ACCELEROMETER))
	        	    	{
	        	    		flagMonitoramento = true;
	        	    	}
	        	    	contA = contA + 1;
	    			}
	    			if(!flagFimDadosGiroscopio && timerAtualG <= miliTimeAtual)
	    			{
	    				// Verificando se esta acontAecendo algum desmaio ou ataque epileptico...
	        			//Log.i(TAG, "G#" + timerG.get(contG) + "|" + eixoXG.get(contG) + "|" + eixoYG.get(contG) + "|" + eixoZG.get(contG));
	    				textViewTimer.setText("Timer: " + timerAtualG);
	    				textViewXG.setText("Posicao GX: " + eixoX_G.get(contG).intValue() + " Float: " + eixoX_G.get(contG));
	    	            textViewYG.setText("Posicao GY: " + eixoY_G.get(contG).intValue() + " Float: " + eixoY_G.get(contG));
	    	            textViewZG.setText("Posicao GZ: " + eixoZ_G.get(contG).intValue() + " Float: " + eixoZ_G.get(contG));
	        	    	if(objHeuristica.monitorar(eixoX_G.get(contG), eixoY_G.get(contG), eixoZ_G.get(contG), Sensor.TYPE_GYROSCOPE))
	        	    	{
	        	    		flagMonitoramento = true;
	        	    	}
	        	    	contG = contG + 1;
	    			}
	    			//Log.i(TAG, "CONTG: " + Integer.toString(contG) + "=" + Integer.toString(timerG.size()) + "|CONTA: " + Integer.toString(contA) + "=" + Integer.toString(timerA.size()));
	    			//Log.i(TAG, "timerG: " + Double.toString(timerAtualG) + "|timerA: " + Double.toString(timerAtualA));
	    			
	    			// Verifica... se ainda há necessidade de sincronizar dados...
					if(contA >= timer_A.size())
					{
						contA = timer_A.size() - 1;
						flagFimDadosAcelerometro = true;
					}
						
					if(contG >= timer_G.size())
					{
						contG = timer_G.size() - 1;
						flagFimDadosGiroscopio = true;
					}
	    			
					timerAtualA = timer_A.get(contA);
					timerAtualG = timer_G.get(contG);
	    			if(!(timerAtualA <= miliTimeAtual || timerAtualG <= miliTimeAtual) || (flagFimDadosAcelerometro && flagFimDadosGiroscopio))
	    			{
	    				flagSincronizarDados = false;
	    			}
    	    	}
    		
				if(!(flagFimDadosAcelerometro && flagFimDadosGiroscopio))
				{
					handler.postDelayed(this, 30);
					return;
				}
				else
				{
	        		if(flagMonitoramento)
	        		{
	        			textViewStatus.append("# " + chaveTeste + " - DESMAIO DETECTADO!!!!\n");
	        			salvarLog(chaveTeste, "DESMAIO DETECTADO!!!!");
	        		}
	        		else
	        		{
	        			textViewStatus.append("# " + chaveTeste + " - OK\n");
	        			salvarLog(chaveTeste, "OK");
	        		}
	        		
	        		txtChaveTeste.setText(""); // libera para proxima thread exibir seus valores e validar o processamento...
				}
    	    }
    	};
    	
        handler.postDelayed(runnable, 100);
        sleepTimer(100);
    }
    
    private void sleepTimer(long time)
    {
    	try
		{
			Thread.sleep(time);
		}
		catch(Exception e)
		{
			// nao faz nada... :-D
		}
    }

    private void salvarLog(String chaveTeste, String resultadoTeste)
    {
    	try
    	{
    		/* Salvando os dados em um log dentro do CARD SD... */
    		File diretorio = new File(pathArquivoLogResultado);
            if (!diretorio.exists()) {  
            	diretorio.mkdirs(); //mkdir() cria somente um diretório, mkdirs() cria diretórios e subdiretórios.  
            } 
    		
    		File arqLog = new File(pathArquivoLogResultado, "log_Automated_Test_Heuristic_" + chaveNomeArquivoLog + ".txt");
            FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			escreverLog.write(chaveTeste.getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());
			escreverLog.write(resultadoTeste.getBytes());
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
	
	private String getChaveArquivoLog() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}
}