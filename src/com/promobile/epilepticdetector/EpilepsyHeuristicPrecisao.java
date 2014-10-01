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
import android.widget.Toast;

public class EpilepsyHeuristicPrecisao {

	String pathArquivoLogResultado = "";
	
	private final boolean MODO_DEBUG = false;

	private long miliTimeInicial;
	
	private Boolean flagGyroscopeAtivado = false;
	private final double ACELERACAO_NORMAL_GRAVIDADE = 9.8;
   
    private final int CONVULSOES_ESTADO_INICIAL = 0;
    private final int CONVULSOES_ESTADO_1 = 1;
    private final int CONVULSOES_ESTADO_2 = 2;
    private int convulsoesEstadoAtual = CONVULSOES_ESTADO_INICIAL;

    double convulsoesPicoAceleracaoEstado1 = ACELERACAO_NORMAL_GRAVIDADE;
    double convulsoesPicoAceleracaoEstado2 = 0;
    
    long convulsoesTimestampEstadoInicial = 0;
    long convulsoesTimestampEstado1 = 0;
	long convulsoesTimestampEstado2 = 0;
	long convulsoesTempoValidacao = 0;
	long convulsoesTempoValidacaoPico = 0;
	
	int contadorMargemErroPicosConvulsoesValidas = 0;
	private final double LIMITE_ACELERACAO_PICO_INFERIOR_CONVULSOES = 8;
	private final double LIMITE_ACELERACAO_PICO_SUPERIOR_CONVULSOES = 11;
	private final double MARGEM_ERRO_AMPLITUDE_ACELERACAO_CONVULSOES = 4;
	private final int MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_CONVULSOES = 8000; // (X/1000) segundos...
	private final int MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_PICOS_CONVULSOES = 160; // (X/1000) segundos...
	private final double MARGEM_ERRO_QTD_MINIMA_ESPASMOS_SEGUNDO = 2; // quantidade de espasmos por segundo.
	private final double MARGEM_ERRO_AMOSTRAGEM_MENOR_VELOCIDADE_FINAL = 4.0;
    
    private final int DESMAIO_ESTADO_INICIAL = 0;
    private final int DESMAIO_ESTADO_1 = 1;
    private final int DESMAIO_ESTADO_2 = 2;
    private final int DESMAIO_ESTADO_3 = 3;
    private final int DESMAIO_ESTADO_4 = 4;
    private int desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
    
    long desmaioTimestampEstado1 = 0;
	long desmaioTimestampEstado2 = 0;
	long desmaioTimestampEstado3 = 0;
	long desmaioTimestampEstado4 = 0;
	long desmaioTimestampAdicionalEstado4 = 0;
	long desmaioTempoValidacao = 0;
	
	double menorModuloAceleracao = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade... 
	double maiorModuloAceleracao = 0;
	double maiorModuloGiroscopio = 0;
	
	private final double LIMITE_ACELERACAO_PICO_INFERIOR = 5;
	private final double LIMITE_ACELERACAO_PICO_SUPERIOR = 18;
	private final double MARGEM_ERRO_AMPLITUDE_ACELERACAO = 14;
	private final double MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO = 0.8;

	private final int MARGEM_ERRO_TEMPO_MINIMO_QUEDA_DESMAIO = 40;
	private final int MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO = 1000;
	private final int MARGEM_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO = 6000;
	private final int MARGEM_ERRO_TEMPO_ADICIONAL_VALIDACAO_DESMAIO = 10000;
	private final int QTD_TOTAL_AMOSTRAGEM_ACELERACAO = 60;
	Stack<Double> arrayAmostragemAceleracao = new Stack<Double>();
	Stack<Double> arrayTimelineAceleracao = new Stack<Double>();

	// AQUI SERAH FEITO UM CALCULO DE INTEGRAL A PARTIR DA ACELERACAO PARA SE OBTER A VELOCIDADE
	private final int QTD_TOTAL_AMOSTRAGEM_VELOCIDADE = 60;
	Stack<Double> arrayAmostragemVelocidade = new Stack<Double>();
	private final int QTD_TOTAL_AMOSTRAGEM_BUFFER_VELOCIDADE_FINAL = 20;
	Stack<Double> arrayBufferVelocidadeFinal = new Stack<Double>();
	private final int QTD_TOTAL_AMOSTRAGEM_VELOCIDADE_FINAL = 60;
	Stack<Double> arrayVelocidadeFinal = new Stack<Double>();
	boolean flagVelocidadeFinalAlcancada = false;

	private final int MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO = 5;
	int contadorMargemErroDesmaio = 0;
	
	private final int ID_EIXO_X = 1;
	private final int ID_EIXO_Y = 2;
	private final int ID_EIXO_Z = 3;
	
	private final int ID_EIXO_X_POSITIVO =  1 * ID_EIXO_X;
	private final int ID_EIXO_X_NEGATIVO = -1 * ID_EIXO_X;
	private final int ID_EIXO_Y_POSITIVO =  1 * ID_EIXO_Y;
	private final int ID_EIXO_Y_NEGATIVO = -1 * ID_EIXO_Y;
	private final int ID_EIXO_Z_POSITIVO =  1 * ID_EIXO_Z;
	private final int ID_EIXO_Z_NEGATIVO = -1 * ID_EIXO_Z;
	private final int QTD_TOTAL_AMOSTRAGEM_EIXO_ACELERACAO = 100;
	
	Stack<Double> eixoNormalAceleracaoAntesX = new Stack<Double>();
	Stack<Double> eixoNormalAceleracaoAntesY = new Stack<Double>();
	Stack<Double> eixoNormalAceleracaoAntesZ = new Stack<Double>();

	Stack<Double> eixoNormalAceleracaoDepoisX = new Stack<Double>();
	Stack<Double> eixoNormalAceleracaoDepoisY = new Stack<Double>();
	Stack<Double> eixoNormalAceleracaoDepoisZ = new Stack<Double>();

	private double maxVariacaoGyroscopeEixoX = 0;
	private double maxVariacaoGyroscopeEixoY = 0;
	private double maxVariacaoGyroscopeEixoZ = 0;
	private final double LIMITE_GIROSCOPIO_PICO_SUPERIOR = 2.0;
	
	boolean flagHabilitarLogs;
	String chaveNomeArquivoLog = "";
	private final String CATACTER_SEPARADOR_ARQ_LOGS = ";";
     
    private Context objContext;

    // Iniciando objetos de musica do android...
	Uri objNotification;
    Ringtone objRing;
   
	// Construtor da classe...
	public EpilepsyHeuristicPrecisao(Context context, boolean habilitarLogs) {
        /********************************************************************************
         *			HEURISTICA DE DETECCAO DE DESMAIOS E ATAQUES EPILEPTICOS			*
         ********************************************************************************/
        flagHabilitarLogs = habilitarLogs;
        flagGyroscopeAtivado = false;
        chaveNomeArquivoLog = getChaveArquivoLog();

        // Obtendo instante inicial do log...
        miliTimeInicial = System.currentTimeMillis();
        
        // Inicializando variaveis do monitoramento...
        resetarVariaveisMonitoramentoDesmaio();
        resetarVariaveisMonitoramentoConvulsoes();

        // Inicializando o servico...
        objContext = context;

        /** BEGIN: Iniciando objetos de musica do android... **/
    	objNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        objRing = RingtoneManager.getRingtone(context, objNotification);
        /** BEGIN: Iniciando objetos de musica do android... **/

        pathArquivoLogResultado = Environment.getExternalStorageDirectory().toString() + "/logs_EpilepsyApp/";
	}
    
    /**
     * Esta funcao calcula a velocidade instantanea a partir da integracao do modulo da aceleracao...
     * @return
     */
    private double calcularVelocidadeInstantanea(Stack<Double> arrayModuloAceleracao, Stack<Double> arrayTimelineAceleracao) {
    	double velocidadeInstantanea = 0;
    	double aceleracaoN = 0;
    	double aceleracaoNless1 = 0;
    	double timelineN = 0;
    	double timelineNless1 = 0;

    	// Verificando se já possui dois pontos dentro das amostragem coletadas na aceleracao.
		if(arrayModuloAceleracao.size() >= 2)
		{
	    	aceleracaoN = arrayModuloAceleracao.get(arrayModuloAceleracao.size() - 1) - ACELERACAO_NORMAL_GRAVIDADE;
	    	aceleracaoNless1 = arrayModuloAceleracao.get(arrayModuloAceleracao.size() - 2) - ACELERACAO_NORMAL_GRAVIDADE;
	    	timelineN = arrayTimelineAceleracao.get(arrayModuloAceleracao.size() - 1);
	    	timelineNless1 = arrayTimelineAceleracao.get(arrayModuloAceleracao.size() - 2);

	    	velocidadeInstantanea = (Math.abs(aceleracaoNless1) + ((Math.abs(aceleracaoN - aceleracaoNless1)/2)) * (timelineN - timelineNless1));        
	    	return(velocidadeInstantanea);
		}
    	return(0);
    }

	/**
	 * Funcao responsavel por realizar o monitoramento de demaios
	 * e ataques epilepticos.
	 * @param event
	 * 
	 * QUANDO FOR FEITO O MONITORAMENTO COM PRECISAO.
	 * http://www.klebermota.eti.br/2012/08/26/sensores-de-movimento-no-android-traducao-da-documentacao-oficial/
	 */
    public boolean monitorar(double x, double y, double z, int typeSensor) {
    	double moduloVetorAceleracao = ACELERACAO_NORMAL_GRAVIDADE;
    	double velocidadeInstantanea = 0;
    	double velocidadeFinal = 0;
    	double moduloVetorGiroscopio = 0;
    	double maiorVariacaoAceleracao = 0;
    	double maiorVelocidadeFinal = 0;

        // Instante de tempo que o log foi gerado...
        long timestampAtualSistema = System.currentTimeMillis();
		double miliTimeAtual = (timestampAtualSistema - miliTimeInicial) / 1000.0;
    	
		// Calculando os modulos resultantes dos eixos x, y e z
		double moduloVetor = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        switch (typeSensor) {
	    	case Sensor.TYPE_ACCELEROMETER:
	    		// Calculando os modulos resultantes dos eixos x, y e z
	    		moduloVetorAceleracao = moduloVetor;
	            
	        	// Atualizando array de amostragens da aceleracao...
	    		if(arrayAmostragemAceleracao.size() >= QTD_TOTAL_AMOSTRAGEM_ACELERACAO)
	    		{
	    			arrayAmostragemAceleracao.pop();
	    			arrayTimelineAceleracao.pop();
	    		}
	    		arrayAmostragemAceleracao.add(0, moduloVetorAceleracao);
	    		arrayTimelineAceleracao.add(0, miliTimeAtual);

	    		// Calculando a velocidade instantanea a partir da integracao do sinal da aceleracao.
	    		velocidadeInstantanea = calcularVelocidadeInstantanea(arrayAmostragemAceleracao, arrayTimelineAceleracao);
	    		
	    		// Atualizando array de amostragens da velocidade...
	    		if(arrayAmostragemVelocidade.size() >= QTD_TOTAL_AMOSTRAGEM_VELOCIDADE)
	    		{
	    			arrayAmostragemVelocidade.pop();
	    		}
	    		arrayAmostragemVelocidade.add(0, velocidadeInstantanea);
	    		
	    		// Atualizando Buffer da velocidade Final...
	    		if(arrayBufferVelocidadeFinal.size() >= QTD_TOTAL_AMOSTRAGEM_BUFFER_VELOCIDADE_FINAL)
	    		{
	    			arrayBufferVelocidadeFinal.pop();
	    		}
	    		arrayBufferVelocidadeFinal.add(0, velocidadeInstantanea);
	    		
	    		// Atualizando Velocidade Final...
	    		if(arrayVelocidadeFinal.size() >= QTD_TOTAL_AMOSTRAGEM_VELOCIDADE_FINAL)
	    		{
	    			arrayVelocidadeFinal.pop();
	    		}
	    		
	    		velocidadeFinal = 0;
	    		for(int cont=0; cont<arrayBufferVelocidadeFinal.size(); cont++){
	    			velocidadeFinal += arrayBufferVelocidadeFinal.get(cont);
	            }
	    		arrayVelocidadeFinal.add(0, velocidadeFinal);
	    		
	    		// Obtendo o menor e o maior pico de aceleracao... quando a maquina de estado for iniciada.
	    		if(desmaioEstadoAtual != DESMAIO_ESTADO_INICIAL)
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
	            		desmaioTimestampEstado2 = timestampAtualSistema; // atualiza o instante em que ocorreu o maior pico de aceleracao...
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
	    	    	menorModuloAceleracao = ACELERACAO_NORMAL_GRAVIDADE; // Aceleracao normal da gravidade.
	    	    	maiorModuloAceleracao = 0;

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
		            CriaArquivosLogAceleracao(miliTimeAtual, x, y, z, moduloVetorAceleracao, velocidadeFinal);
	    		}
	    	break;
	    	
	    	case Sensor.TYPE_GYROSCOPE:
	    		flagGyroscopeAtivado = true;

	    		// Calculando os modulos resultantes dos eixos x, y e z
	    		moduloVetorGiroscopio = moduloVetor;
	    		
	    		if(desmaioEstadoAtual != DESMAIO_ESTADO_INICIAL)
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

	    		// Obtendo o menor e o maior pico do giroscopio... quando a maquina de estado for iniciada.
	    		if(desmaioEstadoAtual != DESMAIO_ESTADO_INICIAL)
	    		{
	    			// Detecta o maior pico do giroscopio
	    			if(moduloVetorGiroscopio >= maiorModuloGiroscopio)
	            		maiorModuloGiroscopio = moduloVetorGiroscopio;
	    		}
	    		else
	    		{
	    			maiorModuloGiroscopio = 0;
	    		}

	    		if(flagHabilitarLogs)
	    		{
	    			// Gerando os Logs dos dados coletados no giroscopio...
	    			CriaArquivosLogGiroscopio(miliTimeAtual, x, y, z, moduloVetorGiroscopio);
	    		}
	    	break;
    	}

        /********************************************************************************
         *						HEURISTICA DE DETECCAO DE DESMAIO						*
         ********************************************************************************/
        // Obtem o modulo do maior pico de variacao do acelerometro...
        maiorVariacaoAceleracao = obterMaiorVariacaoAceleracao();

        switch (desmaioEstadoAtual) {
			case DESMAIO_ESTADO_1:
                if(moduloVetorAceleracao >= LIMITE_ACELERACAO_PICO_SUPERIOR) // Verifica se alcancou o pico superior
            	{
                	if(MODO_DEBUG)
                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_1 -> DESMAIO_ESTADO_2", Toast.LENGTH_SHORT).show();
    	        	
    	        	desmaioEstadoAtual = DESMAIO_ESTADO_2;
    	        	desmaioTimestampEstado2 = timestampAtualSistema;
    	        	maiorModuloAceleracao = moduloVetorAceleracao;
            	}
    	        else if(maiorVariacaoAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO) // Verificar se o sinal do acelerometro estabilizou... atraves de uma margem de erro em relacao a normal 9,8 
            	{
                	if(MODO_DEBUG)
                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_1 -> DESMAIO_ESTADO_INICIAL", Toast.LENGTH_SHORT).show();
    	        	
    	        	desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
    	        	resetarVariaveisMonitoramentoDesmaio();
            	}
			break;
			
			case DESMAIO_ESTADO_2:
				// Verificando se a velocidade do evento bate com o limiar de um desmaio.
				maiorVelocidadeFinal = obterMaiorVelocidadeFinal();
				if(maiorVelocidadeFinal >= MARGEM_ERRO_AMOSTRAGEM_MENOR_VELOCIDADE_FINAL)
				{
					flagVelocidadeFinalAlcancada = true;
				}
				
            	// Aguarda a estabilizacao do sinal do acelerometro... atraves de uma margem de erro em relacao a normal 9,8 
    	        if(maiorVariacaoAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
    	        {
    	        	desmaioTimestampEstado3 = timestampAtualSistema;
    	        	long tempoTotalEntrePicosQueda = desmaioTimestampEstado2 - desmaioTimestampEstado1;

    	        	// Apenas debug...
    	        	String msgText = " TEMPO_Q(" + Long.toString(tempoTotalEntrePicosQueda) + ")";

    	        	if(tempoTotalEntrePicosQueda >= MARGEM_ERRO_TEMPO_MINIMO_QUEDA_DESMAIO && flagVelocidadeFinalAlcancada) // Validando tempo minimo de queda...
    	        	{
    	        		desmaioEstadoAtual = DESMAIO_ESTADO_3;
        	        	if(MODO_DEBUG)
                    		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_2 -> DESMAIO_ESTADO_3" + msgText, Toast.LENGTH_SHORT).show();
    	        	}
    	        	else
    	        	{
    	        		desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
        	        	if(MODO_DEBUG)
                    		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_2 -> DESMAIO_ESTADO_INICIAL" + msgText, Toast.LENGTH_SHORT).show();
    	        	}
    	        }
			break;
			
			case DESMAIO_ESTADO_3:
            	// Obtendo amplitude de aceleracao entre o menor pico e o maior pico...
            	double amplitudeAceleracao = maiorModuloAceleracao - menorModuloAceleracao;
            	boolean flagHabilitaEstado4 = false;
            	
            	if(!flagGyroscopeAtivado && amplitudeAceleracao > MARGEM_ERRO_AMPLITUDE_ACELERACAO)
            		flagHabilitaEstado4 = true;
            	else if(flagGyroscopeAtivado && amplitudeAceleracao > MARGEM_ERRO_AMPLITUDE_ACELERACAO && maiorModuloGiroscopio >= LIMITE_GIROSCOPIO_PICO_SUPERIOR)
            		flagHabilitaEstado4 = true;
            	
            	if(flagHabilitaEstado4)
        		{
                	if(MODO_DEBUG)
                	{
                		String msgText = " A(" + Double.toString(amplitudeAceleracao) + ") G("+Double.toString(maiorModuloGiroscopio)+")";
                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_3 -> DESMAIO_ESTADO_4" + msgText, Toast.LENGTH_SHORT).show();
                	}

    	        	desmaioEstadoAtual = DESMAIO_ESTADO_4;
            		desmaioTimestampEstado4 = timestampAtualSistema;
            		desmaioTimestampAdicionalEstado4 = 0;
            		
	                if(!objRing.isPlaying()) /** Emitindo beep... para validar um possivel desmaio... **/
		        		objRing.play();
        		}
            	else
            	{
                	if(MODO_DEBUG)
                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_3 -> DESMAIO_ESTADO_INICIAL", Toast.LENGTH_SHORT).show();

    	        	desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
    	        	resetarVariaveisMonitoramentoDesmaio();
            		return false;
            	}
			break;
			
			case DESMAIO_ESTADO_4:
            	desmaioTempoValidacao = timestampAtualSistema - desmaioTimestampEstado4;
            	if(desmaioTempoValidacao > MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_DESMAIO)
            	{
    	        	if(desmaioTempoValidacao < (MARGEM_ERRO_TEMPO_TOTAL_VALIDACAO_DESMAIO + desmaioTimestampAdicionalEstado4))
    	        	{
    	        		double variacaoVetorAceleracaoAtual = Math.abs(moduloVetorAceleracao - ACELERACAO_NORMAL_GRAVIDADE);
    	        		
    	        		// Verificar se está havendo alguma oscilação no acelerometro... caso contrario o cara apagou mesmo... :P
    	    	        if(variacaoVetorAceleracaoAtual > MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
    	    	        	contadorMargemErroDesmaio++;
    	    	        
    	    	        if(contadorMargemErroDesmaio > MARGEM_ERRO_CONTADOR_VARIACOES_DESMAIO)
    	    	        {
    	                	if(MODO_DEBUG)
    	                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_4 -> DESMAIO_ESTADO_INICIAL", Toast.LENGTH_SHORT).show();

    	    	        	desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
    	    	        	resetarVariaveisMonitoramentoDesmaio();
    	    	        }
    	        	}
    	        	else
    	        	{
    	        		int eixoNormalAntes = obterEixoNormal(eixoNormalAceleracaoAntesX, eixoNormalAceleracaoAntesY, eixoNormalAceleracaoAntesZ);
    	        		int eixoNormalDepois = obterEixoNormal(eixoNormalAceleracaoDepoisX, eixoNormalAceleracaoDepoisY, eixoNormalAceleracaoDepoisZ);
    	        		
    	        		// A condicao abaixo verifica se a pessoa estava de pé e deitou ou virou... ou seja, a pessoa não está na mesma posicao antes do impacto.
    	        		if(eixoNormalAntes != eixoNormalDepois)
    	        		{
    	                	if(MODO_DEBUG)
    	                	{
    	                		String msgTexto = "ANTES(" + Integer.toString(eixoNormalAntes) + ") DEPOIS(" + Integer.toString(eixoNormalDepois) + ")";
    	                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_4 -> " + msgTexto, Toast.LENGTH_SHORT).show();
    	                	}

    	                	// Verificando se houve alguma variacao angular no giroscopio...
    	                	boolean flagAtivarAlertaDesmaio = true;
        	        		if(flagGyroscopeAtivado)
    	        			{
	        					switch (Math.abs(eixoNormalAntes)) {
	        						case ID_EIXO_X:
	        							if(maxVariacaoGyroscopeEixoY >= LIMITE_GIROSCOPIO_PICO_SUPERIOR || maxVariacaoGyroscopeEixoZ >= LIMITE_GIROSCOPIO_PICO_SUPERIOR)
	        								flagAtivarAlertaDesmaio = true;
	        							else
	        								flagAtivarAlertaDesmaio = false;
    	        			    	break;
	        						case ID_EIXO_Y:
	        							if(maxVariacaoGyroscopeEixoX >= LIMITE_GIROSCOPIO_PICO_SUPERIOR || maxVariacaoGyroscopeEixoZ >= LIMITE_GIROSCOPIO_PICO_SUPERIOR)
	        								flagAtivarAlertaDesmaio = true;
	        							else
	        								flagAtivarAlertaDesmaio = false;
    	        			    	break;
	        						case ID_EIXO_Z:
	        							if(maxVariacaoGyroscopeEixoX >= LIMITE_GIROSCOPIO_PICO_SUPERIOR || maxVariacaoGyroscopeEixoY >= LIMITE_GIROSCOPIO_PICO_SUPERIOR)
	        								flagAtivarAlertaDesmaio = true;
	        							else
	        								flagAtivarAlertaDesmaio = false;
    	        			    	break;
	        					}
	    	        		}

        	        		if(flagAtivarAlertaDesmaio)
        	        		{
        	                	if(MODO_DEBUG)
        	                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_4 -> DESMAIO_ESTADO_INICIAL -> DESMAIO DETECTADO(1)", Toast.LENGTH_SHORT).show();

        	                	desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
		        	        	resetarVariaveisMonitoramentoDesmaio();
	    			        	return true; // TEM MAIORES CHANCES DE SER UM DESMAIO...
        	        		}
    	        		}

    	        		if(desmaioTimestampAdicionalEstado4 == 0)
    	        		{
	                    	if(MODO_DEBUG)
	                    		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_4 -> TEMPO ADICIONAL DE VALIDACAO DESMAIO", Toast.LENGTH_SHORT).show();

	                    	desmaioTimestampAdicionalEstado4 = MARGEM_ERRO_TEMPO_ADICIONAL_VALIDACAO_DESMAIO;
    	        		}
    	        		else
    	        		{
	                    	if(MODO_DEBUG)
	                    		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_4 -> DESMAIO_ESTADO_INICIAL -> DESMAIO DETECTADO(2)", Toast.LENGTH_SHORT).show();
	
		    	        	desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
	        	        	resetarVariaveisMonitoramentoDesmaio();
	    	        		return true;
    	        		}
    	        	}
            	}
            	else
            	{
            		contadorMargemErroDesmaio = 0;
            	}
			break;
			
			default: // Estado Inicial
				desmaioEstadoAtual = DESMAIO_ESTADO_INICIAL;
            	if(moduloVetorAceleracao <= LIMITE_ACELERACAO_PICO_INFERIOR)
                {
                	if(MODO_DEBUG)
                		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_INICIAL -> DESMAIO_ESTADO_1", Toast.LENGTH_SHORT).show();

    	        	desmaioEstadoAtual = DESMAIO_ESTADO_1;
	            	desmaioTimestampEstado1 = timestampAtualSistema;
	                menorModuloAceleracao = moduloVetorAceleracao;
                }
			break;
		}
        /** FIM - HEURISTICA DE DETECCAO DE DESMAIO	**/
		
//        /********************************************************************************
//         *						HEURISTICA DE DETECCAO DE CONVULSOES					*
//         ********************************************************************************/
//        // Obtem o modulo do maior pico de variacao do acelerometro...
//        maiorVariacaoAceleracao = obterMaiorVariacaoAceleracao();
//
//        switch (convulsoesEstadoAtual) {
//			case CONVULSOES_ESTADO_1: // BORDA DE DESCIDA...
//                // Verificar se o sinal do acelerometro estabilizou... atraves de uma margem de erro em relacao a normal 9,8 
//                if(maiorVariacaoAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
//            	{
//                	if(MODO_DEBUG)
//                		Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_1 -> CONVULSOES_ESTADO_INICIAL", Toast.LENGTH_SHORT).show();
//    	        	
//                	convulsoesEstadoAtual = CONVULSOES_ESTADO_INICIAL;
//                	resetarVariaveisMonitoramentoConvulsoes();
//            	}
//                else
//                {
//	                // VERIFICANDO BORDA DE DESCIDA...
//	                if(moduloVetorAceleracao <= LIMITE_ACELERACAO_PICO_INFERIOR_CONVULSOES)
//	                {
//	            		if(moduloVetorAceleracao <= convulsoesPicoAceleracaoEstado1)
//		            	{
//		            		convulsoesPicoAceleracaoEstado1 = moduloVetorAceleracao;
//		            	}
//		            	else // Significar que tem que pegar o outro pico do gráfico do acelerometro...
//		            	{
////		            		if(MODO_DEBUG)
////		                		Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_1 -> CONVULSOES_ESTADO_2", Toast.LENGTH_SHORT).show();
//		            		
//		            		convulsoesEstadoAtual = CONVULSOES_ESTADO_2;
//		            		convulsoesTimestampEstado2 = timestampAtualSistema;
//		            		convulsoesPicoAceleracaoEstado2 = moduloVetorAceleracao;
//		            	}
//	                }
//                }
//			break;
//			
//			case CONVULSOES_ESTADO_2:  // BORDA DE SUBIDA...
//                // Verificar se o sinal do acelerometro estabilizou... atraves de uma margem de erro em relacao a normal 9,8 
//                if(maiorVariacaoAceleracao <= MARGEM_ERRO_AMOSTRAGEM_ACELERACAO_SINAL_ESTABILIZADO)
//            	{
//                	if(MODO_DEBUG)
//                		Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_2 -> CONVULSOES_ESTADO_INICIAL", Toast.LENGTH_SHORT).show();
//    	        	
//                	convulsoesEstadoAtual = CONVULSOES_ESTADO_INICIAL;
//                	resetarVariaveisMonitoramentoConvulsoes();
//            	}
//                else
//                {
//	                // VERIFICANDO BORDA DE SUBIDA...
//	                if(moduloVetorAceleracao >= LIMITE_ACELERACAO_PICO_SUPERIOR_CONVULSOES)
//	                {
//	                	if(moduloVetorAceleracao >= convulsoesPicoAceleracaoEstado2)
//		            	{
//		            		convulsoesPicoAceleracaoEstado2 = moduloVetorAceleracao;
//		            	}
//		            	else // Significar que tem que pegar o outro pico do gráfico do acelerometro...
//		            	{
//		            		// VERIFICANDO SINAIS CONVULSIVOS...
//		            		double convulsoesModuloAmplitudeVetorAceleracao = Math.abs(convulsoesPicoAceleracaoEstado2 - convulsoesPicoAceleracaoEstado1);
//		            		convulsoesTempoValidacaoPico = timestampAtualSistema - convulsoesTimestampEstado1;
//		            		convulsoesTempoValidacao = timestampAtualSistema - convulsoesTimestampEstadoInicial;
//	            			
//		            		if(convulsoesModuloAmplitudeVetorAceleracao >= MARGEM_ERRO_AMPLITUDE_ACELERACAO_CONVULSOES &&
//		            		   convulsoesTempoValidacaoPico >= MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_PICOS_CONVULSOES)
//		            		{
//		            			contadorMargemErroPicosConvulsoesValidas++;
//		            			if(convulsoesTempoValidacao > MARGEM_ERRO_TEMPO_MINIMO_VALIDACAO_CONVULSOES)
//		            			{
//		            				double tempoValidacaoSegudos = convulsoesTempoValidacao / 1000;
//		            				double qtdConvulsoesSegundo = contadorMargemErroPicosConvulsoesValidas / tempoValidacaoSegudos;
//		            				
//		            				if(MODO_DEBUG)
//		            					Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_2 Espasmos/s (" + String.valueOf(qtdConvulsoesSegundo) + ")", Toast.LENGTH_SHORT).show();
//		            				
//		            				if(qtdConvulsoesSegundo >= MARGEM_ERRO_QTD_MINIMA_ESPASMOS_SEGUNDO)
//		            				{
//		            					if(MODO_DEBUG)
//		        		            		Toast.makeText(objContext, "EpilepsyApp - DESMAIO_ESTADO_2 -> DESMAIO_ESTADO_INICIAL -> CONVULSAO DETECTADA(1)", Toast.LENGTH_SHORT).show();
//		        		
//		        						convulsoesEstadoAtual = CONVULSOES_ESTADO_INICIAL;
//		        		            	resetarVariaveisMonitoramentoConvulsoes();
//		        		            	return true;
//		            				}
//		            			}
//		            			
//			            		
//		            			if(MODO_DEBUG)
//			                		Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_2 -> CONVULSOES_ESTADO_1 (" + 
//			                				String.valueOf(contadorMargemErroPicosConvulsoesValidas) + ", " +
//			                				String.valueOf(convulsoesTempoValidacaoPico) + ", " +
//			                				String.valueOf(convulsoesTempoValidacao) + ", " +
//			                				String.valueOf(convulsoesModuloAmplitudeVetorAceleracao), Toast.LENGTH_SHORT).show();
//		            		}
//		            		
//		            		convulsoesEstadoAtual = CONVULSOES_ESTADO_1;
//		            		convulsoesTimestampEstado1 = timestampAtualSistema;
//		            		convulsoesPicoAceleracaoEstado1 = moduloVetorAceleracao;
//		            	}
//	                }
//                }
//			break;
//
//			default: // Estado Inicial
//				convulsoesEstadoAtual = CONVULSOES_ESTADO_INICIAL;
//
//                // VERIFICANDO BORDA DE DESCIDA...
//                if(moduloVetorAceleracao <= LIMITE_ACELERACAO_PICO_INFERIOR_CONVULSOES)
//                {
////            		if(MODO_DEBUG)
////                		Toast.makeText(objContext, "EpilepsyApp - CONVULSOES_ESTADO_INICIAL -> CONVULSOES_ESTADO_1", Toast.LENGTH_SHORT).show();
//            		
//                	convulsoesEstadoAtual = CONVULSOES_ESTADO_1;
//                	convulsoesPicoAceleracaoEstado1 = moduloVetorAceleracao;
//                	convulsoesTimestampEstadoInicial = timestampAtualSistema;
//                	convulsoesTimestampEstado1 = timestampAtualSistema;
//                }
//			break;
//		}
//        /** FIM - HEURISTICA DE DETECCAO DE CONVULSOES	**/

        return false;
    }
    
    /**
     *  reinicializa os dados do monitoramento de demaios.
     */
    private void resetarVariaveisMonitoramentoDesmaio() {
    	desmaioTimestampEstado1 = 0;
    	desmaioTimestampEstado2 = 0;
    	desmaioTimestampEstado3 = 0;
    	desmaioTimestampEstado4 = 0;
    	desmaioTimestampAdicionalEstado4 = 0;
    	desmaioTempoValidacao = 0;

    	contadorMargemErroDesmaio = 0;

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
    	
    	flagVelocidadeFinalAlcancada = false;
    }
    
    /**
     *  reinicializa os dados do monitoramento de convulsoes.
     */
    private void resetarVariaveisMonitoramentoConvulsoes() {
    	convulsoesTimestampEstadoInicial = 0;
    	convulsoesTimestampEstado1 = 0;
    	convulsoesTimestampEstado2 = 0;
    	convulsoesTempoValidacao = 0;
    	convulsoesTempoValidacaoPico = 0;
    	
    	convulsoesPicoAceleracaoEstado1 = 0;
    	convulsoesPicoAceleracaoEstado2 = 0;
    	
    	contadorMargemErroPicosConvulsoesValidas = 0;
    }
    
    /**
     *  Esta funcao retorna a porcentagem da maior variacao da aceleracao... Ex.: -0.21, 0.50, 0.01, etc
     * @return
     */
    private double obterMaiorVariacaoAceleracao() {
    	double maiorVetorAceleracao = 0;
    	double maiorVariacaoAceleracao = 0;
    	
    	for(Double valorAceleracao : arrayAmostragemAceleracao) {
    		if(valorAceleracao >= maiorVetorAceleracao)
    			maiorVetorAceleracao = valorAceleracao;
    	}
    	
    	maiorVariacaoAceleracao = Math.abs(maiorVetorAceleracao - ACELERACAO_NORMAL_GRAVIDADE);
    	return(maiorVariacaoAceleracao);
    }
    
    /**
     *  Esta funcao retorna a maior velocidade final mapeada dentro da janela de amostragem.
     * @return
     */
    private double obterMaiorVelocidadeFinal() {
    	double maiorVelocidadeFinal = 0;

    	for(Double valorVelocidade : arrayVelocidadeFinal) {
    		if(valorVelocidade >= maiorVelocidadeFinal)
    			maiorVelocidadeFinal = valorVelocidade;
    	}
    	return(maiorVelocidadeFinal);
    }
    
    /**
     *  A funcao retorna o eixo normal do celular, atraves de comparacoes entre os eixos X, Y e Z
     * @param eixoX
     * @param eixoY
     * @param eixoZ
     * @return
     */
    private int obterEixoNormal(Stack<Double> eixoX, Stack<Double> eixoY, Stack<Double> eixoZ) {
    	double totalAceleracaoEixoX_Positivo = 0;
    	double totalAceleracaoEixoX_Negativo = 0;
    	double totalAceleracaoEixoY_Positivo = 0;
    	double totalAceleracaoEixoY_Negativo = 0;
    	double totalAceleracaoEixoZ_Positivo = 0;
    	double totalAceleracaoEixoZ_Negativo = 0;

    	double totalAceleracaoEixoX = 0;
    	double totalAceleracaoEixoY = 0;
    	double totalAceleracaoEixoZ = 0;
    	
    	for(Double valorAceleracao : eixoX) {
    		if(valorAceleracao >= 0)
    			totalAceleracaoEixoX_Positivo += Math.abs(valorAceleracao);
    		else
    			totalAceleracaoEixoX_Negativo += Math.abs(valorAceleracao);
    	}
    	for(Double valorAceleracao : eixoY) {
    		if(valorAceleracao >= 0)
    			totalAceleracaoEixoY_Positivo += Math.abs(valorAceleracao);
    		else
    			totalAceleracaoEixoY_Negativo += Math.abs(valorAceleracao);
        }
    	for(Double valorAceleracao : eixoZ) {
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

    private void CriaArquivosLogAceleracao(Double miliTimeAtual, Double x, Double y, Double z, Double moduloVetor, Double velocidadeFinal)
    {
    	try
    	{
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		File logDirectory = new File(pathArquivoLogResultado);
            if (!logDirectory.exists()) {  
            	logDirectory.mkdirs(); //mkdir() cria somente um diretório, mkdirs() cria diretórios e subdiretórios.  
            } 
    		File arqLog = new File(logDirectory, "logsEpilepsyApp_Accelerometer_" + chaveNomeArquivoLog + ".txt");
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
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(velocidadeFinal).getBytes());
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

    private void CriaArquivosLogGiroscopio(double miliTimeAtual, double x, double y, double z, double moduloVetor)
    {
    	try
    	{
    		/** BEGIN: Gerando grafico de analise do acelerometro **/
    		File logDirectory = new File(pathArquivoLogResultado);
            if (!logDirectory.exists()) {  
            	logDirectory.mkdirs(); //mkdir() cria somente um diretório, mkdirs() cria diretórios e subdiretórios.  
            } 
    		File arqLog = new File(logDirectory, "logsEpilepsyApp_Gyroscope_" + chaveNomeArquivoLog + ".txt");
			FileOutputStream escreverLog = new FileOutputStream(arqLog, true);
			
			// Instante de tempo que o log foi gerado...
			escreverLog.write(Double.toString(miliTimeAtual).getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(x).getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(y).getBytes());
			escreverLog.write(CATACTER_SEPARADOR_ARQ_LOGS.getBytes());

			escreverLog.write(Double.toString(z).getBytes());
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
