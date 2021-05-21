package com.example.mannaprototype;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.mannaprototype.models.ResidentModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        FirebaseFirestore.getInstance().collection("residents")
                .whereEqualTo("userType", "Resident")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful() && task.getResult() != null) {
                        List<MarkerOptions> markers = new ArrayList<>();
                        task.getResult().getDocuments().forEach(documentSnapshot -> {
                            ResidentModel residentModel = documentSnapshot.toObject(ResidentModel.class);
                            if (residentModel != null && residentModel.getLatitude() != null && residentModel.getLongitude() != null) {
                                MarkerOptions option = new MarkerOptions().position(new LatLng(residentModel.getLatitude(), residentModel.getLongitude())).title(residentModel.getFullName());
                                mMap.addMarker(option);
                                markers.add(option);
                            }
                        });

                        LatLngBounds.Builder b = new LatLngBounds.Builder();
                        for (MarkerOptions m : markers) {
                            b.include(m.getPosition());
                        }
                        LatLngBounds bounds = b.build();

//                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25,25,5);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));

                    }
                });
    }
}