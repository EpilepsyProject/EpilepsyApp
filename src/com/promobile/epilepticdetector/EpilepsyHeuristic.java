package com.promobile.epilepticdetector;

import android.content.Context;

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
	 * 
	 * QUANDO FOR FEITO O MONITORAMENTO COM PRECISAO.
	 * http://www.klebermota.eti.br/2012/08/26/sensores-de-movimento-no-android-traducao-da-documentacao-oficial/
	 */
    public boolean monitorar(double x, double y, double z, int typeSensor) {
    	if(perfil == PERFIL_MODERADO) {
    		return(objPerfilModerado.monitorar(x, y, z, typeSensor));
    	}
    	else if(perfil == PERFIL_PRECISAO) {
    		return(objPerfilPrecisao.monitorar(x, y, z, typeSensor));
    	}
    	else {
    		// Perfil de monitoramento nao existe... :-P
    		return false;
    	}
    }
}
