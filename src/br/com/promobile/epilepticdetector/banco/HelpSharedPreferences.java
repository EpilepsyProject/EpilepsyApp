package br.com.promobile.epilepticdetector.banco;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HelpSharedPreferences {
	private SharedPreferences preferences;
	public HelpSharedPreferences(SharedPreferences prefs) {
		this.preferences =  prefs;	
	}
	
	
	//gets
	public int getTempo(){
		return preferences.getInt("id_tempo", 0); 		//Recebe o valor do índice 'id_tempo' 
	}
	public String getSom(){
		return preferences.getString("id_som", "Nda."); //Recebe a valor do índice 'id_som' 
	}
	
	//sets
	public void setTempo(int valor){ 
    	Editor editor = preferences.edit(); //Cria um editor para alterar o valor do índice
    	editor.putInt("id_tempo", valor);	//Através do put() String insere um valor string no índice
    	editor.commit();					//Salva os dados
	}
	public void setSom(String valor){
		Editor editor = preferences.edit(); //Cria um editor para alterar o valor do índice 
		editor.putString("id_som", valor);  //Através do put() String insere um valor string no índice 
		editor.commit(); 					//Salva os dados
	}
	public void configuracaoPadrao(){ 		
		setTempo(300);
		setSom("Psiu!");
	}
}
