package com.promobile.epilepticdetector;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;

public class HistoricoActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        TextView  tv=new TextView(this);
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("TAB HISTÓRICO");
        
        setContentView(tv);
    }


}
