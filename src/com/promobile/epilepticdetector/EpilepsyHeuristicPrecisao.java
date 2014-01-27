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

public class EpilepsyHeuristicPrecisao {

	private long miliTimeInicial;
	
	private Boolean flagGyroscopeAtivado = false;
    
    /************* Estagios da deteccao de desmaio ******************/
    private Boolean flagEstagio1 = false;
    private Boolean flagEstagio2 = false;
    private Boolean flagEstagio3 = false;
    private Boolean flagEstagio4 = false;

    private Boolean flagContagemPicos = false;
    private int qtdTotalPicos = 0;
    private final int QTD_MINIMA_PICOS_DESMAIO = 1;
    
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
	
	private final double LIMITE_PICO_INFERIOR = 7;
	private final double LIMITE_PICO_SUPERIOR = 11;
	private final int MARGEM_ERRO_TEMPO_ACELERACAO_DESACELERACAO = 30; // EM MILISEGUNDOS...
	private final int MARGEM_ERRO_DESACELERACAO_SINAL_ESTABILIZADO = 200;
	private final int MARGEM_ERRO_TEMPO_TOTAL_QUEDA_SINAL_ESTABILIZADO = 400; // EM MILISEGUNDOS...
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
    //******** End: Estagios da deteccao de desmaio
   
	// Construtor da classe...
	public EpilepsyHeuristicPrecisao(Context context, SensorManager sensorManager, boolean habilitarLogs) {
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
	 * 
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
	    		{
	    			arrayAmostragemAceleracao.pop();
	    		}
	    		arrayAmostragemAceleracao.add(0, moduloVetorAceleracao);
	    		
	    		/**************** BEGIN: COLETANDO DADOS DOS EIXOS... BASEADO NO HISTORICO PASSADO... ********************/
	    		if(flagEstagio1 == true || flagEstagio2 == true || flagEstagio3 == true || flagEstagio4 == true)
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
	    		else
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
	    		/**************** END: COLETANDO DADOS DOS EIXOS... BASEADO NO HISTORICO PASSADO... ********************/
	    		
	            /********************************************************************************
	             *						HEURISTICA DE DETECCAO DE DESMAIO						*
	             ********************************************************************************/
	            if(flagEstagio4 == true)
	            {
	            	double tempoTotalValidacaoDesmaio = timestampAtualSistema - timestampEstagio4;

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
	    	    	        	resetarVariaveisMonitoramento();
	    	    	        }
	    	        	}
	    	        	else
	    	        	{
	    	        		int eixoNormalAntes = obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ);
	    	        		int eixoNormalDepois = obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ);
	    	        		
	    	        		// A condicao abaixo verifica se a pessoa estava de pé e deitou ou virou... ou seja, a pessoa não está na mesma posicao antes do impacto.
	    	        		if(eixoNormalAntes != eixoNormalDepois)//TODO: && Math.abs(eixoNormalAntes) != Math.abs(eixoNormalDepois))
	    	        		{
	    	        			Boolean flagAtivarAlertaDesmaio = true;
	    	        			
	    	        			// Verificando se houve alguma variacao angular no giroscopio...
	    	        			if(flagGyroscopeAtivado)
	    	        			{
    	        					switch (Math.abs(eixoNormalAntes)) {
    	        						case ID_EIXO_X:
    	        							if(maxVariacaoGyroscopeEixoY >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ >= MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlertaDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlertaDesmaio = false;
    	        							}
	    	        			    	break;
    	        						case ID_EIXO_Y:
    	        							if(maxVariacaoGyroscopeEixoX >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoZ >= MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlertaDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlertaDesmaio = false;
    	        							}
	    	        			    	break;
    	        						case ID_EIXO_Z:
    	        							if(maxVariacaoGyroscopeEixoX >= MARGEM_ERRO_MINIMO_GYROSCOPE || maxVariacaoGyroscopeEixoY >= MARGEM_ERRO_MINIMO_GYROSCOPE)
    	        							{
    	        								flagAtivarAlertaDesmaio = true;
    	        							}
    	        							else
    	        							{
    	        								flagAtivarAlertaDesmaio = false;
    	        							}
	    	        			    	break;
    	        					}
	    	        			}
	    	        			
	    	        			if(flagAtivarAlertaDesmaio)
	    	        			{
		    			        	resetarVariaveisMonitoramento();
		    			        	
		    			        	// Exibindo alerta de desmaio...
		    			        	return true;
	    	        			}
	    	        			else
	    	        			{
		    	    	        	resetarVariaveisMonitoramento();
	    	        			}
	    	        		}
	    	        		else
	    	        		{
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
	        	        else if(moduloVetorAceleracao >= LIMITE_PICO_SUPERIOR)
	                	{
	        	        	flagContagemPicos = false;    	        	
	        	        	if(moduloVetorAceleracao >= moduloAceleracaoEstagio2)
	        	        	{
	    	                	flagEstagio2 = true;
	    	            		timestampEstagio2 = timestampAtualSistema;
	    	            		intervaloEstagio1e2 = timestampEstagio2 - timestampEstagio1;
	    	
	    	            		moduloAceleracaoEstagio2 = moduloVetorAceleracao;
	        	        	}
	                	}
	                }

	                if(flagEstagio2 == true)
	                {
	                	// Verificar se o cara apagou... atraves de uma margem de erro em relacao a normal 9,8 
	        	        double mediaAmostralAceleracao = Math.abs(obterMediaVariacaoAceleracao());
	        	        if(mediaAmostralAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
	        	        {
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
	                		if(intervaloEstagio1e2 >= MARGEM_ERRO_TEMPO_ACELERACAO_DESACELERACAO && intervaloEstagio2e3 >= MARGEM_ERRO_DESACELERACAO_SINAL_ESTABILIZADO && intervaloEstagio1e3 >= MARGEM_ERRO_TEMPO_TOTAL_QUEDA_SINAL_ESTABILIZADO && qtdTotalPicos >= QTD_MINIMA_PICOS_DESMAIO)
	                		{
	    	            		flagEstagio4 = true;
	    	            		timestampEstagio4 = timestampAtualSistema;

	    	            		/** BEGIN: Iniciando objetos de musica do android... **/
	    		                if(!objRing.isPlaying())
	    			        	{
	    			        		objRing.play();
	    			        	}
	    		                /** BEGIN: Iniciando objetos de musica do android... **/
	                		}
	                    	else
	                    	{
	                    		resetarVariaveisMonitoramento();
	                    	}
	                	}
	                }
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
