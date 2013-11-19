package com.promobile.epilepticdetector;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.ListActivity;
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

public class ListView_Config<ListViewAdapterSessions, TechnicalSession> extends ListActivity {

	ListView listview;
	String[] itens_config = {"Alertas", "Gerenciador de contatos"};
	private ArrayAdapter<String> mAdaptador;
	Intent intent;
	private ListView techSessions;
	private ListViewAdapterSessions adapter;
	public Context context;
	private ArrayList<TechnicalSession> techSessionList; 
	public static String tag;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listview = (ListView)findViewById(R.id.list1);				
		 mAdaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itens_config);
	     listview.setAdapter(mAdaptador);
	     listview.setOnItemClickListener(new OnItemClickListener(){ //Esse método é usado para pegar um item da lista que foi escolhido pelo usuário.
				
				public void onItemClick(AdapterView<?>parent, View view, int position, long id){
					//ListView Clicked item index
					int itemPosition = position; //Cada item da lista tem um índice, essa linha pega esse índice
					
					//ListView Clicked item value
					TechnicalSession objectSession = (TechnicalSession)techSessions.getItemAtPosition(itemPosition); //Essa linha pega o item de acordo com o índice que foi salvo em 'itemPosition'
					
					Bundle bundle = new Bundle(); //Crio algo para guardar meus objetos
					bundle.putSerializable("objectSession", (Serializable) objectSession); //Adiciona o item no bundle (adiciono o meu objeto (item da lista))
					intent = new Intent(ListView_Config.this,AlertasActivity.class); //crio uma intent (ela chama a outra tela)
					intent.putExtras(bundle); //Adiciona o bundle na intent (o meu item (objeto) está no bundle)
			        startActivity(intent);  //chama a outra tela
				}
			});
	    
	     TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
			tabHost.setup();
			
			final TabWidget tabWidget = tabHost.getTabWidget();
			final FrameLayout tabContent = tabHost.getTabContentView();
			
			// Get the original tab textviews and remove them from the viewgroup.
			TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
			for (int index = 0; index < tabWidget.getTabCount(); index++) {
				originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
			}
			tabWidget.removeAllViews();
			
			// Ensure that all tab content childs are not visible at startup.
			for (int index = 0; index < tabContent.getChildCount(); index++) {
				tabContent.getChildAt(index).setVisibility(View.GONE);
			}
			
			// Create the tabspec based on the textview childs in the xml file.
			// Or create simple tabspec instances in any other way...
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
	}



}
