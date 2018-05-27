package in.pannu.harpal.notes;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String Latitude;
    String Longitude;
    String Title;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (getIntent().getStringExtra("Longitude") != null) {
            Longitude = getIntent().getStringExtra("Longitude");
            Latitude  = getIntent().getStringExtra("Latitude");
            Title = getIntent().getStringExtra("Title");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void onMapReady(GoogleMap googleMap) {
        Double dLatitude = Double.parseDouble(Latitude);
        Double dLongitude = Double.parseDouble(Longitude);
        LatLng Location = new LatLng(dLatitude, dLongitude);
        googleMap.addMarker(new MarkerOptions().position(Location).title(Title));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 20.0f));
    }
}
