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
 
public class MainActivity<ListViewAdapterSessions, TechnicalSession> extends Activity {
	
	ListView listview;
	String[] itens_config = {"Alertas", "Gerenciador de contatos"};
	
	Intent intent;
	
	public Context context;
	private ArrayAdapter<String> mAdaptador;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		listview = (ListView)findViewById(R.id.list1);				
		 mAdaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itens_config);
	     listview.setAdapter(mAdaptador);	         
	    
	     listview.setOnItemClickListener(new OnItemClickListener() {  
	    	  
	         @Override  
	         public void onItemClick(AdapterView<?> adapter, View view,  
	                 int position, long id) {  
	             int pos = position;  
	             if (pos == 0) {  
	                 Intent intent = new Intent(MainActivity.this, AlertasActivity.class);  
	                 startActivity(intent);  
	             }
	             else if(pos == 1){
	            	 Intent intent = new Intent(MainActivity.this, ContatosActivity.class);  
	                 startActivity(intent); 
	             }
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
