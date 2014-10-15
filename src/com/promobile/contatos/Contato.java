package com.promobile.contatos;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.promobile.epilepticdetector.GPSTracker;

public class Contato {    
  public String id;
  public String nome;
  public List<Telefone> telefones;
  private  String telefone;
  
  Context context;
  
  String phoneNumber;
  GPSTracker gps;
  Handler handler;
  SmsManager sms;
  
  public Contato(){
  }
  
  public Contato(Context context){
	  this.context = context;
  }
  
  public void setNome(String nome) {
	this.nome = nome;
  }
  
  public void setTelefone(String telefone){
	this.telefone = telefone;
  }
  public String getTelefone() {
	return telefone;
  }
  
  
  /**
   * Método responsável por enviar sms para cada contato cadastrado no aplicativo. 
   */
  public void enviarSmsParaContatos(){
  	//Manda um sms para cada contato cadastrado na tela de configurações 
		for (int i = 2; i <= 4; i++) {
			SharedPreferences pref = context.getSharedPreferences("prefs_do_contato", Context.MODE_PRIVATE);
			String destino = pref.getString("telefoneKey"+i, "Nda");
			
			if(!destino.equals("Nda")){
				enviaSms(destino);
			}
		}
		SharedPreferences pref = context.getSharedPreferences("prefs_do_contato", Context.MODE_PRIVATE);
		String destino = pref.getString("telefoneKey"+1, "Nda");
		
		if(!destino.equals("Nda")){
			ligarContato(destino);
		}
  }
  
  public void enviaSms(String destino){
		sms = SmsManager.getDefault();
		phoneNumber = destino;

  	handler = new Handler();
  	gps = new GPSTracker(context);
  	
  	Runnable runnable = new Runnable() {
  		public void run(){
  			double latitude = gps.getLatitude();
  			double longitude = gps.getLongitude();
  	    	
  			if(latitude != 0 && longitude != 0){
  				String mensagem = "Ataque epiletico detectado. http://maps.google.com/?q=" + String.valueOf(latitude) + "," +  String.valueOf(longitude);
  				sms.sendTextMessage(phoneNumber, null, mensagem, null, null);
  			}
  			else{
  				handler.postDelayed(this, 100);
					return;
  			}
  	    	
  			Toast.makeText(context.getApplicationContext(), "EpilepsyApp - Mensagem Enviada para " + phoneNumber, Toast.LENGTH_SHORT).show();
  			return;
  	    }
  	};
  	
      handler.postDelayed(runnable, 100);
	}
  
  public void ligarContato(String destino){
  	Uri uri = Uri.parse("tel:"+ destino);
	Intent intent = new Intent(Intent.ACTION_CALL,uri);
	context.startActivity(intent);
  }

  
  
}