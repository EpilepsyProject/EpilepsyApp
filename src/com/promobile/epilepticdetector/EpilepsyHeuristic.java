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

public class EpilepsyHeuristic {

	public static int PERFIL_MODERADO = 0;
	public static int PERFIL_PRECISAO = 1;
	private int perfil = PERFIL_MODERADO;
	
	private EpilepsyHeuristicModerado objPerfilModerado;
	private EpilepsyHeuristicPrecisao objPerfilPrecisao;
	
	// Construtor da classe...
	public EpilepsyHeuristic(Context context, SensorManager sensorManager, int perfilMonitoramento, boolean habilitarLogs) {
		perfil = perfilMonitoramento;
		objPerfilModerado = new EpilepsyHeuristicModerado(context, sensorManager, habilitarLogs);
		objPerfilPrecisao = new EpilepsyHeuristicPrecisao(context, sensorManager, habilitarLogs);
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
