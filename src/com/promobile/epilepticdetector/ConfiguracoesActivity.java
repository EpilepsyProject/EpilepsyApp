package com.promobile.epilepticdetector;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ConfiguracoesActivity extends Activity {


	private ArrayAdapter<String> mAdaptador;
	String[] itens_config = {"Alertas", "Gerenciador de contatos"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracoes);
		//listview aponta para o id list do tipo ListView  
		
   	 ListView listview = (ListView)findViewById(R.id.list1);                
        //aqui � usado um ArrayAdapter para poder fazer com que telas diferentes sejam chamadas
        //ao clicar em cada uma das op��es do listview
        mAdaptador = new ArrayAdapter<String>(this,R.layout.list_view_itens, itens_config);
        listview.setAdapter(mAdaptador);                 
        //in�cio da fun��o p/ chamar diferentes telas ao selecionar um item do listview
        listview.setOnItemClickListener(new OnItemClickListener() {  
             
        @Override  
        public void onItemClick(AdapterView<?> adapter, View view,  
                int position, long id) {  
            int pos = position;  //aqui recebe a posi��o de cada tab e seu id
            if (pos == 0) {  //se a posi��o da tab selecionada for 0 (a primeira)
                    //chama a tela de alertas
                    //declara a chamada de telas
                Intent intent = new Intent(ConfiguracoesActivity.this, AlertasActivity.class);  
                startActivity(intent); //chama a tela AlertasActivity 
            }
            if(pos == 1){  //se a posi��o da tab selecionada for 1 (a segunda)
                    //chama a tela de contatos
                    //declara a chamada de telas
                    Intent intent = new Intent(ConfiguracoesActivity.this, ContatosActivity.class);  
                startActivity(intent); //chama a tela ContatosActivity
            }
          /*  if(pos == 2){  //se a posi��o da tab selecionada for 2 (a terceira)
                //chama a tela de ativar app
                //declara a chamada de telas
                Intent intent = new Intent(ConfiguracoesActivity.this, AtivarAPPActivity.class);  
                startActivity(intent); //chama a tela ContatosActivity
        }*/
        }  
    }); 

	}
}
