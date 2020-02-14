package com.medium.progettomedium;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.medium.progettomedium.Fragment.SearchFragment;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    private Marker marker;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ChildEventListener mChildEventListener;
        mUsers = FirebaseDatabase.getInstance().getReference("Eventi");
        mUsers.push().setValue(marker);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(40.1199325, 9.0104808));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    DatabaseEvento evento = s.getValue(DatabaseEvento.class);
                    LatLng location = new LatLng(evento.latitude, evento.longitude);
                    marker = mMap.addMarker(new MarkerOptions().position(location).title(evento.titolo));
                    marker.setTag(evento.getId());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Eventi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseEvento databaseEvento = snapshot.getValue(DatabaseEvento.class);
                    if (databaseEvento.getId().equals(marker.getTag())) {
                        String mTitolo = databaseEvento.getTitolo();
                        String mLuogo = databaseEvento.getLuogo();
                        String mDescrizione = databaseEvento.getDescrizione();
                        String mImage = databaseEvento.getImmagine();
                        String mData = databaseEvento.getDate();
                        String mId = databaseEvento.getId();
                        Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                        intent.putExtra("title", mTitolo);
                        intent.putExtra("description", mLuogo);
                        intent.putExtra("descrizione", mDescrizione);
                        intent.putExtra("image", mImage);
                        intent.putExtra("date", mData);
                        intent.putExtra("id", mId);
                        startActivity(intent);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
}
