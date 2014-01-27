package com.promobile.epilepticdetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;

public class EpilepsyHeuristicModerado {

	private long miliTimeInicial;
	
	private Boolean flagGyroscopeAtivado = false;
    
    private final int ESTADO_INICIAL = 0;
    private final int ESTADO_1 = 1;
    private final int ESTADO_2 = 2;
    private final int ESTADO_3 = 3;
    private final int ESTADO_4 = 4;
    private int estadoAtual = ESTADO_1;
    
    long timestampEstado1 = 0;
	long timestampEstado2 = 0;
	long timestampEstado3 = 0;
	long timestampEstado4 = 0;
	long tempoValidacao = 0;
	
	private final double ACELERACAO_NORMAL_GRAVIDADE = 9.8;
	double menorModuloAceleracao = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade... 
	double maiorModuloAceleracao = 0;
	
	private final double LIMITE_PICO_INFERIOR = 7;
	private final double LIMITE_PICO_SUPERIOR = 11;
	private final int MARGEN_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO = 1500;
	private final int MARGEN_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO = 6000;
	private final double MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO = 0.8;
	private final double MARGEM_ERRO_AMPLITUDE_ACELERACAO = 4;
	private final int QTD_TOTAL_AMOSTRAGEM_ACELERACAO = 30;
	Stack<Double> arrayAmostragemAceleracao = new Stack<Double>();

	private final int MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO = 5;
	private final double MARGEM_ERRO_ACELERACAO_DESMAIO = MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO;
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

	private final double MARGEM_ERRO_MINIMO_GYROSCOPE = 2.5;
	private double maxVariacaoGyroscopeEixoX = 0;
	private double maxVariacaoGyroscopeEixoY = 0;
	private double maxVariacaoGyroscopeEixoZ = 0;
	
	boolean flagHabilitarLogs;
	String chaveNomeArquivoLog = "";
	private final String CATACTER_SEPARADOR_ARQ_LOGS = ";";
     
    Float x, y, z;
    Float xGiroscopio, yGiroscopio, zGiroscopio;

    private Context objContext;

    // Iniciando objetos de musica do android...
	Uri objNotification;
    Ringtone objRing;
   
	// Construtor da classe...
	public EpilepsyHeuristicModerado(Context context, SensorManager sensorManager, boolean habilitarLogs) {
        /********************************************************************************
         *			HEURISTICA DE DETECCAO DE DESMAIOS E ATAQUES EPILEPTICOS			*
         ********************************************************************************/
        flagHabilitarLogs = habilitarLogs;
        flagGyroscopeAtivado = false;
        chaveNomeArquivoLog = getChaveArquivoLog();

        // Obtendo instante inicial do log...
        miliTimeInicial = System.currentTimeMillis();
        
        // Inicializando variaveis do monitoramento...
        resetarVariaveisMonitoramento();

        // Inicializando o servico...
        objContext = context;

        /** BEGIN: Iniciando objetos de musica do android... **/
    	objNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        objRing = RingtoneManager.getRingtone(context, objNotification);
        /** BEGIN: Iniciando objetos de musica do android... **/
	}

	/**
	 * Funcao responsavel por realizar o monitoramento de demaios
	 * e ataques epilepticos.
	 * @param event
	 */
    public boolean monitorar(SensorEvent event) {
    	x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        // Instante de tempo que o log foi gerado...
        long timestampAtualSistema = System.currentTimeMillis();
		double miliTimeAtual = (timestampAtualSistema - miliTimeInicial) / 1000.0;

		// Calculando os modulos resultantes dos eixos x, y e z
		double moduloVetor = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        switch (event.sensor.getType()) {
        
	    	case Sensor.TYPE_ACCELEROMETER:
	    		// Calculando os modulos resultantes dos eixos x, y e z
	    		double moduloVetorAceleracao = moduloVetor;
	            
	        	// Atualizando array de amostragens da aceleracao...
	    		if(arrayAmostragemAceleracao.size() >= QTD_TOTAL_AMOSTRAGEM_ACELERACAO)
	    			arrayAmostragemAceleracao.pop();

	    		arrayAmostragemAceleracao.add(0, moduloVetorAceleracao);
	    		
	            /********************************************************************************
	             *						HEURISTICA DE DETECCAO DE DESMAIO						*
	             ********************************************************************************/
            	double mediaAmostralAceleracao = Math.abs(obterMediaVariacaoAceleracao());
	    		switch (estadoAtual) {
					case ESTADO_1:
	                	// Verificar se o sinal do acelerometro estabilizou... atraves de uma margem de erro em relacao a normal 9,8 
		        	    if(mediaAmostralAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
	        	        {
	        	        	resetarVariaveisMonitoramento();
	        	        }
	        	        else if(moduloVetorAceleracao >= LIMITE_PICO_SUPERIOR) // Verifica se alcancou o pico superior
	                	{
	        	        	estadoAtual = ESTADO_2;
	        	        	timestampEstado2 = timestampAtualSistema;
	                	}
					break;
					
					case ESTADO_2:
	                	// Aguarda a estabilizacao do sinal do acelerometro... atraves de uma margem de erro em relacao a normal 9,8 
	        	        if(mediaAmostralAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
	        	        {
	        	        	estadoAtual = ESTADO_3;
	        	        	timestampEstado3 = timestampAtualSistema;
	        	        }
					break;
					
					case ESTADO_3:
	                	// Obtendo amplitude de aceleracao entre o menor pico e o maior pico...
	                	double amplitudeAceleracao = maiorModuloAceleracao - menorModuloAceleracao;
	                	if(amplitudeAceleracao > MARGEM_ERRO_AMPLITUDE_ACELERACAO)
                		{
                			estadoAtual = ESTADO_4;
    	            		timestampEstado4 = timestampAtualSistema;
    	            		
    		                if(!objRing.isPlaying()) /** EMITINDO BIPE... **/
    			        		objRing.play();
                		}
                    	else
                    		resetarVariaveisMonitoramento();
					break;
					
					case ESTADO_4:
		            	tempoValidacao = timestampAtualSistema - timestampEstado4;
		            	if(tempoValidacao > MARGEN_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO)
		            	{
		    	        	if(tempoValidacao < MARGEN_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO)
		    	        	{
		    	        		// Verificar se houve alguma oscilação no acelerometro... caso contrario o cara apagou mesmo... :P
		    	    	        if(mediaAmostralAceleracao > MARGEM_ERRO_ACELERACAO_DESMAIO)
		    	    	        	contadoMargemErroDesmaio++;
		    	    	        
		    	    	        if(contadoMargemErroDesmaio > MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO)
		    	    	        	resetarVariaveisMonitoramento();
		    	        	}
		    	        	else
		    	        	{
		    	        		int eixoNormalAntes = obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ);
		    	        		int eixoNormalDepois = obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ);
		    	        		
		    	        		// A condicao abaixo verifica se a pessoa estava de pé e deitou ou virou... ou seja, a pessoa não está na mesma posicao antes do impacto.
		    	        		if(eixoNormalAntes != eixoNormalDepois)//TODO: && Math.abs(eixoNormalAntes) != Math.abs(eixoNormalDepois))
		    	        		{
		    	        			boolean flagAtivarAlertaDesmaio = true;
		    	        			
		    	        			// Verificando se houve alguma variacao angular no giroscopio...
		    	        			if(flagGyroscopeAtivado)
		    	        			{
	    	        					switch (Math.abs(eixoNormalAntes)) {
	    	        						case ID_EIXO_X:
	    	        							if(maxVariacaoGyroscopeEixoY >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ >= MARGEM_ERRO_MINIMO_GYROSCOPE)
	    	        								flagAtivarAlertaDesmaio = true;
	    	        							else
	    	        								flagAtivarAlertaDesmaio = false;
		    	        			    	break;
	    	        						case ID_EIXO_Y:
	    	        							if(maxVariacaoGyroscopeEixoX >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ >= MARGEM_ERRO_MINIMO_GYROSCOPE)
	    	        								flagAtivarAlertaDesmaio = true;
	    	        							else
	    	        								flagAtivarAlertaDesmaio = false;
		    	        			    	break;
	    	        						case ID_EIXO_Z:
	    	        							if(maxVariacaoGyroscopeEixoX >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoY >= MARGEM_ERRO_MINIMO_GYROSCOPE)
	    	        								flagAtivarAlertaDesmaio = true;
	    	        							else
	    	        								flagAtivarAlertaDesmaio = false;
		    	        			    	break;
	    	        					}
		    	        			}
		    	        			
		    	        			if(flagAtivarAlertaDesmaio)
		    	        			{
		    	        				resetarVariaveisMonitoramento();
			    			        	return true; // TEM MAIORES CHANCES DE SER UM DESMAIO...
		    	        			}
		    	        		}

		    	        		resetarVariaveisMonitoramento();
		    	        		return true; // TEM MENORES CHANCES DE SER UM DESMAIO...
		    	        	}
		            	}
		            	else
		            	{
		            		contadoMargemErroDesmaio = 0;
		            	}
					break;
					
					default: // Estado Inicial
		            	if(moduloVetorAceleracao <= LIMITE_PICO_INFERIOR)
		                {
	    	                estadoAtual = ESTADO_1;
	    	            	timestampEstado1 = timestampAtualSistema;
	    	                menorModuloAceleracao = moduloVetorAceleracao;
		                }
					break;
				}
	    		
	    		// Obtendo o menor e o maior pico de aceleracao... quando a maquina de estado for iniciada.
	    		if(estadoAtual != ESTADO_INICIAL)
	    		{
	    			// Detecta o menor pico de aceleracao
	    			if(moduloVetorAceleracao < menorModuloAceleracao)
	    			{
	    				menorModuloAceleracao = moduloVetorAceleracao;
	    			}
	    			// Detecta o maior pico de aceleracao
	    			if(moduloVetorAceleracao >= maiorModuloAceleracao)
    	        	{
	            		maiorModuloAceleracao = moduloVetorAceleracao;
    	        	}

	    			// Coletando as variacoes de aceleracao de cada eixo (X, Y e Z) durante a queda.
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
	    		else
	    		{
	    			// Coletando as variacoes de aceleracao de cada eixo (X, Y e Z) antes da queda.
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
	    		
	    		if(flagHabilitarLogs)
	    		{
		            // Gerando os Logs dos dados coletados no acelerometro...
		            CriaArquivosLogAceleracao(miliTimeAtual, x, y, z, moduloVetorAceleracao);
	    		}
	    	break;
	    	
	    	case Sensor.TYPE_GYROSCOPE:
	    		flagGyroscopeAtivado = true;

	    		// Calculando os modulos resultantes dos eixos x, y e z
	    		double moduloVetorGiroscopio = moduloVetor;
	    		
	    		if(estadoAtual != ESTADO_INICIAL)
	    		{
	    			double moduloX = Math.abs(x);
	    			double moduloY = Math.abs(y);
	    			double moduloZ = Math.abs(z);
	    			
	    			if(moduloX > maxVariacaoGyroscopeEixoX)
	    				maxVariacaoGyroscopeEixoX = moduloX;
	    			
	    			if(moduloY > maxVariacaoGyroscopeEixoY)
	    				maxVariacaoGyroscopeEixoY = moduloY;
	    			
	    			if(moduloZ > maxVariacaoGyroscopeEixoZ)
	    				maxVariacaoGyroscopeEixoZ = moduloZ;
	    		}

	    		if(flagHabilitarLogs)
	    		{
	    			// Gerando os Logs dos dados coletados no giroscopio...
	    			CriaArquivosLogGiroscopio(miliTimeAtual, x, y, z, moduloVetorGiroscopio);
	    		}
	    	break;
    	}
        
        return false;
    }
    
    /**
     *  Esta funcao retorna a porcentagem media de variacao da aceleracao... Ex.: -0.21, 0.50, 0.01, etc
     */
    private void resetarVariaveisMonitoramento() {
    	estadoAtual = ESTADO_INICIAL;
    	
    	timestampEstado1 = 0;
    	timestampEstado2 = 0;
    	timestampEstado3 = 0;
    	timestampEstado4 = 0;
    	tempoValidacao = 0;

    	menorModuloAceleracao = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade.
    	maiorModuloAceleracao = 0;
    	
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
    
    /**
     *  Esta funcao retorna a porcentagem media de variacao da aceleracao... Ex.: -0.21, 0.50, 0.01, etc
     * @return
     */
    private double obterMediaVariacaoAceleracao() {
    	double totalVariacaoAceleracao = 0;
    	double mediaVariacaoAceleracao = 0;
    	
    	for(Double valorAceleracao : arrayAmostragemAceleracao) {
    		totalVariacaoAceleracao += valorAceleracao;
    	}
    	
    	mediaVariacaoAceleracao = ACELERACAO_NORMAL_GRAVIDADE - (totalVariacaoAceleracao / QTD_TOTAL_AMOSTRAGEM_ACELERACAO);
    	
    	return(mediaVariacaoAceleracao);
    }
    
    /**
     *  A funcao retorna o eixo normal do celular, atraves de comparacoes entre os eixos X, Y e Z
     * @param eixoX
     * @param eixoY
     * @param eixoZ
     * @return
     */
    private int obterEixoNormal(Stack<Float> eixoX, Stack<Float> eixoY, Stack<Float> eixoZ) {
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
    			totalAceleracaoEixoX_Positivo += Math.abs(valorAceleracao);
    		else
    			totalAceleracaoEixoX_Negativo += Math.abs(valorAceleracao);
    	}
    	for(Float valorAceleracao : eixoY) {
    		if(valorAceleracao >= 0)
    			totalAceleracaoEixoY_Positivo += Math.abs(valorAceleracao);
    		else
    			totalAceleracaoEixoY_Negativo += Math.abs(valorAceleracao);
        }
    	for(Float valorAceleracao : eixoZ) {
    		if(valorAceleracao >= 0)
    			totalAceleracaoEixoZ_Positivo += Math.abs(valorAceleracao);
    		else
    			totalAceleracaoEixoZ_Negativo += Math.abs(valorAceleracao);
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

    private void CriaArquivosLogAceleracao(Double miliTimeAtual, Float x, Float y, Float z, Double moduloVetor)
    {
    	try
    	{
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		/* Salvando os dados em um log dentro do CARD SD... */
    		File arqLog = new File(Environment.getExternalStorageDirectory(), "logsEpilepsyApp_Accelerometer_" + chaveNomeArquivoLog + ".txt");
			FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			escreverLog.write(Double.toString(miliTimeAtual).getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(x.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(y.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(z.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(moduloVetor).getBytes());
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

    private void CriaArquivosLogGiroscopio(Double miliTimeAtual, Float x, Float y, Float z, Double moduloVetor)
    {
    	try
    	{
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		/* Salvando os dados em um log dentro do CARD SD... */
    		File arqLog = new File(Environment.getExternalStorageDirectory(), "logsEpilepsyApp_Gyroscope_" + chaveNomeArquivoLog + ".txt");
			FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			escreverLog.write(Double.toString(miliTimeAtual).getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(x.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(y.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(z.toString().getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(moduloVetor).getBytes());
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
