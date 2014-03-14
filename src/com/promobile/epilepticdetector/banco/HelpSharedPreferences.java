package com.promobile.epilepticdetector.banco;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HelpSharedPreferences {
	private SharedPreferences preferences;
	public HelpSharedPreferences(SharedPreferences prefs) {
		this.preferences =  prefs;	
	}
	
	
	//gets
	public int getTempo(){
		return preferences.getInt("id_tempo", 0); 		//Recebe o valor do �ndice 'id_tempo' 
	}
	public String getSom(){
		return preferences.getString("id_som", "Nda."); //Recebe a valor do �ndice 'id_som' 
	}
	
	//sets
	public void setTempo(int valor){ 
    	Editor editor = preferences.edit(); //Cria um editor para alterar o valor do �ndice
    	editor.putInt("id_tempo", valor);	//Atrav�s do put() String insere um valor string no �ndice
    	editor.commit();					//Salva os dados
	}
	public void setSom(String valor){
		Editor editor = preferences.edit(); //Cria um editor para alterar o valor do �ndice 
		editor.putString("id_som", valor);  //Atrav�s do put() String insere um valor string no �ndice 
		editor.commit(); 					//Salva os dados
	}
	public void configuracaoPadrao(){ 		
		setTempo(300);
		setSom("Psiu!");
	}
}
