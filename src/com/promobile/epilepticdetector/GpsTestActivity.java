//nome do pacote, pode mudar de acordo com a configuração do projeto
package com.promobile.epilepticdetector;
 
//importação das classes necessárias para o funcionamento do aplicativo
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class GpsTestActivity extends MapActivity {
 
    //variáveis que usaremos durante o processo
    private EditText edLatitude;
    private EditText edLongitude;
    private Button btLocalizar;
    
    @Override // Método onCreate, iniciada quando o aplicativo é aberto
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);
        
        MapView mapView = (MapView) findViewById(R.id.map_view) ;
        mapView.setClickable(true) ;
         
        MyLocationOverlay mlo = new MyLocationOverlay(this, mapView) ;
        mlo.enableCompass() ;
        mlo.enableMyLocation() ;
        mapView.getOverlays().add(mlo) ;
    
        setupElements();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
 
    // Método usado para importar os elementos da classe R
    public void setupElements(){
        edLatitude = (EditText) findViewById(R.id.edLatitude);
        edLongitude = (EditText) findViewById(R.id.edLongitude);
 
        btLocalizar = (Button) findViewById(R.id.btLocalizar);
        btLocalizar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                startGPS();
        }});
    }
    //Método que faz a leitura de fato dos valores recebidos do GPS
    public void startGPS(){
        LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                updateView(locat);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }
 
    //  Método que faz a atualização da tela para o usuário.
    public void updateView(Location locat){
        Double latitude = locat.getLatitude();
        Double longitude = locat.getLongitude();
 
        edLatitude.setText(latitude.toString());
        edLongitude.setText(longitude.toString());
    }
}