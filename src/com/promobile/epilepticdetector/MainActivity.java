package com.promobile.epilepticdetector;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {

    // TabSpec Names
        private static final String TAB1 = "Início";
        private static final String TAB2 = "Configurações";
      //  private static final String TAB3 = "Tab3";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            
            
            TabHost tabHost = getTabHost();

            // Inbox Tab
            TabSpec inboxSpec = tabHost.newTabSpec(TAB1);
            Intent inboxIntent = new Intent(this, InicialActivity.class);
            inboxSpec.setIndicator(TAB1);
            // Tab Content
            inboxSpec.setContent(inboxIntent);

            // Outbox Tab
            TabSpec PriceSpec = tabHost.newTabSpec(TAB2);
            Intent PriceIntent = new Intent(this, ConfiguracoesActivity.class);
            PriceSpec .setIndicator(TAB2);
            PriceSpec.setContent(PriceIntent);

          /*  // Profile Tab
            TabSpec DistanceSpec = tabHost.newTabSpec(TAB3);
            Intent DistanceIntent = new Intent(this, Tab3.class);
            DistanceSpec .setIndicator(TAB3); 
            DistanceSpec.setContent(DistanceIntent);*/

            // Adding all TabSpec to TabHost
            tabHost.addTab(inboxSpec); 
            tabHost.addTab(PriceSpec); 
          //  tabHost.addTab(DistanceSpec); 

            //Set the current value tab to default first tab
            tabHost.setCurrentTab(0);

            //Setting custom height for the tabs
            final int height = 45;
            tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = height;
            tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = height;
        //    tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = height;
        }

}