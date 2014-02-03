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
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AutomatedTestHeuristicActivity extends Activity{
	String chaveNomeArquivoLog = "";
	private final String CATACTER_SEPARADOR_ARQ_LOGS = " - ";

	static String TAG = "AutomatedTestHeuristicActivity";

    private EpilepsyHeuristic objHeuristica;
    
    // O path abaixo contAem a lista de arquivos de teste automatizados...
    String pathArquivosTeste = "";
    String pathArquivoLogResultado = "";

    private Context context;
    private Button btnStartTest;
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
        
        context = getApplicationContext();
        setContentView(R.layout.activity_automated_test_heuristic);
         
        textViewTimer = (TextView) findViewById(R.id.txtValorTimer);
        textViewXA = (TextView) findViewById(R.id.txtValorX);
        textViewYA = (TextView) findViewById(R.id.txtValorY);
        textViewZA = (TextView) findViewById(R.id.txtValorZ);
        textViewXG = (TextView) findViewById(R.id.txtValorXG);
        textViewYG = (TextView) findViewById(R.id.txtValorYG);
        textViewZG = (TextView) findViewById(R.id.txtValorZG);
        textViewVetor = (TextView) findViewById(R.id.txtValorVetor);
        textViewStatus = (TextView) findViewById(R.id.text_view_status);
        
        btnStartTest = (Button) findViewById(R.id.btnStartTest);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
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

        /********************************************************************************
         *						HEURISTICA DE DETECCAO DE DESMAIO						*
         ********************************************************************************/
        chaveNomeArquivoLog = getChaveArquivoLog();
        
		// Inicializando o monitoramento...
        objHeuristica = new EpilepsyHeuristic(context, EpilepsyHeuristic.PERFIL_PRECISAO, false);
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
    	        	textViewStatus.setText("Validando novo teste... Aguarde...\n");
    	        	
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

    	        	String chaveTeste = f.getName().replace("logsEpilepsyApp_Accelerometer_", "").replace(".txt", "");
    	        	String pathDadosTesteAcelerometro =  "logsEpilepsyApp_Accelerometer_" + chaveTeste + ".txt";
    	        	String pathDadosTesteGiroscopio = "logsEpilepsyApp_Gyroscope_" + chaveTeste + ".txt";

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
    	        		boolean flagMonitoramento = false;
    	        		boolean flagFimDadosAcelerometro = false;
    	        		boolean flagFimDadosGiroscopio = false;
	        			contA = 0;
    	        		contG = 0;

	        			while(!(flagFimDadosAcelerometro && flagFimDadosGiroscopio))
    	        		{
	        				double timerAtualA;
	        				double timerAtualG;
	        				
	        				if(contA >= timerA.size())
	        				{
	        					contA = timerA.size() - 1;
	        					flagFimDadosAcelerometro = true;
	        				}
	        					
	        				if(contG >= timerG.size())
	        				{
	        					contG = timerG.size() - 1;
	        					flagFimDadosGiroscopio = true;
	        				}
	        				
	        				timerAtualA = timerA.get(contA);
	        				timerAtualG = timerG.get(contG);
	        				
    	        			if((!flagFimDadosAcelerometro && timerAtualA <= timerAtualG) || flagFimDadosGiroscopio)
    	        			{
    	        				// Verificando se esta acontAecendo algum desmaio ou ataque epileptico...
	    	        			//Log.i(TAG, "A#" + timerA.get(contA) + "|" + eixoXA.get(contA) + "|" + eixoYA.get(contA) + "|" + eixoZA.get(contA));

    	        				textViewTimer.setText("Timer: " + timerAtualA);
    	        				textViewXA.setText("Posicao AX: " + eixoXA.get(contA).intValue() + " Float: " + eixoXA.get(contA));
    	        	            textViewYA.setText("Posicao AY: " + eixoYA.get(contA).intValue() + " Float: " + eixoYA.get(contA));
    	        	            textViewZA.setText("Posicao AZ: " + eixoZA.get(contA).intValue() + " Float: " + eixoZA.get(contA));
    	        	            textViewVetor.setText("Vetor Aceleracao: " + Double.toString(eixoMA.get(contA)));
    	        				
	    	        	    	if(objHeuristica.monitorar(eixoXA.get(contA), eixoYA.get(contA), eixoZA.get(contA), Sensor.TYPE_ACCELEROMETER))
	    	        	    	{
	    	        	    		flagMonitoramento = true;
	    	        	    	}
	    	        	    	contA = contA + 1;
    	        			}
    	        			else if((!flagFimDadosGiroscopio && timerAtualG <= timerAtualA) || flagFimDadosAcelerometro)
    	        			{
    	        				// Verificando se esta acontAecendo algum desmaio ou ataque epileptico...
	    	        			//Log.i(TAG, "G#" + timerG.get(contG) + "|" + eixoXG.get(contG) + "|" + eixoYG.get(contG) + "|" + eixoZG.get(contG));

    	        				textViewTimer.setText("Timer: " + timerAtualG);
    	        				textViewXG.setText("Posicao GX: " + eixoXG.get(contG).intValue() + " Float: " + eixoXG.get(contG));
    	        	            textViewYG.setText("Posicao GY: " + eixoYG.get(contG).intValue() + " Float: " + eixoYG.get(contG));
    	        	            textViewZG.setText("Posicao GZ: " + eixoZG.get(contG).intValue() + " Float: " + eixoZG.get(contG));
    	        				
	    	        	    	if(objHeuristica.monitorar(eixoXG.get(contG), eixoYG.get(contG), eixoZG.get(contG), Sensor.TYPE_GYROSCOPE))
	    	        	    	{
	    	        	    		flagMonitoramento = true;
	    	        	    	}
	    	        	    	contG = contG + 1;
    	        			}
    	        			//Log.i(TAG, "CONTG: " + Integer.toString(contG) + "=" + Integer.toString(timerG.size()) + "|CONTA: " + Integer.toString(contA) + "=" + Integer.toString(timerA.size()));
    	        			//Log.i(TAG, "timerG: " + Double.toString(timerAtualG) + "|timerA: " + Double.toString(timerAtualA));
    	        			
    	        			sleepTimer(10);
     	        		}
    	        		
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
    	        	}
    	        	else
    	        	{
    	        		textViewStatus.append("# " + chaveTeste + " - Um dos arquivos não possui dados!\n");
    	        		salvarLog(chaveTeste, "Um dos arquivos não possui dados!");
    	        	}
    	        	
    	        	sleepTimer(2000);
    	        }
    	    }
    	}
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