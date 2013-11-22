package com.promobile.epilepticdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
 
public class MainActivity extends Activity {
	
	ListView listview;
	String[] itens_config = {"Alertas", "Gerenciador de contatos"};
	Intent intent;	
	public Context context;
	private ArrayAdapter<String> mAdaptador;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main); //chama o layout no arquivo xml da main    	      
	    //tabHost aponta para o id tabhost no arquivo xml
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(); //starta o layout no arquivo xml
		
		final TabWidget tabWidget = tabHost.getTabWidget();
		final FrameLayout tabContent = tabHost.getTabContentView();
	
		// Pega o textview da tab original e o remove do viewgroup.
		TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
		for (int index = 0; index < tabWidget.getTabCount(); index++) {
			originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
		}
		tabWidget.removeAllViews();
		
		// Certifica se todo o conteúdo da tab "filha" não está visível na inicialização
		for (int index = 0; index < tabContent.getChildCount(); index++) {
			tabContent.getChildAt(index).setVisibility(View.GONE);
		}
		
		// Cria o tabspec baseado no textview childs no arquivo xml.
		// Ou cria uma instancia simples de tabspec
		for (int index = 0; index < originalTextViews.length; index++) {
			final TextView tabWidgetTextView = originalTextViews[index];
			final View tabContentView = tabContent.getChildAt(index);
			TabSpec tabSpec = tabHost.newTabSpec((String) tabWidgetTextView.getTag());
			tabSpec.setContent(new TabContentFactory() {
				@Override
				public View createTabContent(String tag) {
					return tabContentView;
				}
			});
			if (tabWidgetTextView.getBackground() == null) {
				tabSpec.setIndicator(tabWidgetTextView.getText());
			} else {
				tabSpec.setIndicator(tabWidgetTextView.getText(), tabWidgetTextView.getBackground());
			}
			tabHost.addTab(tabSpec);
		}	
	
	
	
	//aqui "seta" o id btnShowDialog para a chamada da TelaDesmaio quando o botão for clicado
	findViewById(R.id.btnShowDialog).setOnClickListener( new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//declara a chamada da tela atraves do itent 
			Intent intent = new Intent(MainActivity.this, TelaDesmaio.class);  
             startActivity(intent); //chama a TelaDesmaio			 
		}
	});
	findViewById(R.id.btnUsarAcelerometro).setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MainActivity.this, AcelerometroActivity.class);
			startActivity(intent);
		}
	});
	// listview aponta para o id list1 do tipo ListView
	listview = (ListView)findViewById(R.id.list1);		
	//aqui é usado um ArrayAdapter para poder fazer com que telas diferentes sejam chamadas
	//ao clicar em cada uma das opções do listview
	 mAdaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itens_config);
     listview.setAdapter(mAdaptador);	         
    //início da função p/ chamar diferentes telas ao selecionar um item do listview
     listview.setOnItemClickListener(new OnItemClickListener() {  
    	  
         @Override  
         public void onItemClick(AdapterView<?> adapter, View view,  
                 int position, long id) {  
             int pos = position;  //aqui recebe a posição de cada tab e seu id
             if (pos == 0) {  //se a posição da tab selecionada for 0 (a primeira)
            	 //chama a tela de alertas
            	 //declara a chamada de telas
                 Intent intent = new Intent(MainActivity.this, AlertasActivity.class);  
                 startActivity(intent); //chama a tela AlertasActivity 
             }
             else if(pos == 1){  //se a posição da tab selecionada for 1 (a segunda)
            	 //chama a tela de contatos
            	 //declara a chamada de telas
            	 Intent intent = new Intent(MainActivity.this, ContatosActivity.class);  
                 startActivity(intent); //chama a tela ContatosActivity
             }
         }  
     });  
	}
}
