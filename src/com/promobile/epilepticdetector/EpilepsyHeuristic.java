package com.promobile.epilepticdetector;

import android.content.Context;
import android.hardware.SensorEvent;

public class EpilepsyHeuristic {

	public static int PERFIL_MODERADO = 0;
	public static int PERFIL_PRECISAO = 1;
	private int perfil = PERFIL_MODERADO;
	
	private EpilepsyHeuristicModerado objPerfilModerado;
	private EpilepsyHeuristicPrecisao objPerfilPrecisao;
	
	// Construtor da classe...
	public EpilepsyHeuristic(Context context, int perfilMonitoramento, boolean habilitarLogs) {
		perfil = perfilMonitoramento;
		objPerfilModerado = new EpilepsyHeuristicModerado(context, habilitarLogs);
		objPerfilPrecisao = new EpilepsyHeuristicPrecisao(context, habilitarLogs);
	}

	/**
	 * Funcao responsavel por realizar o monitoramento de demaios
	 * e ataques epilepticos.
	 * 
	 * @param event
	 */
    public boolean monitorar(SensorEvent event) {
    	if(perfil == PERFIL_MODERADO) {
    		return(objPerfilModerado.monitorar(event));
    	}
    	else if(perfil == PERFIL_PRECISAO) {
    		return(objPerfilPrecisao.monitorar(event));
    	}
    	else {
    		// Perfil de monitoramento nao existe... :-P
    		return false;
    	}
    }
}
