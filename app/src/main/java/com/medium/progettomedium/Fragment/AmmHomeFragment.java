package com.medium.progettomedium.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.ActivityDettagliAmm;
import com.medium.progettomedium.Adapter.AdaptEventoAmm;
import com.medium.progettomedium.AddEventoActivity;
import com.medium.progettomedium.AddPostActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

public class AmmHomeFragment extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    boolean doubleTap = false;
    private ArrayList<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private AdaptEventoAmm adapter;
    private AdaptEventoAmm.OnItemClickListener itemClickListener;
    private RecyclerView listaEventiView;
    DatabaseReference mRef;
    private DatabaseReference databaseReference;
    private Button statoprenotazione;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference getDatabaseReferencevento;
    private GestureDetectorCompat mGestureDetector;
    private CardView nessunEve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation_amm);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        listaEventiView = (RecyclerView) findViewById(R.id.row_adda);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();

        nessunEve = findViewById(R.id.nessunEve);
        nessunEve.setVisibility(View.GONE);

        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");

        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Eventi");
        //POPOLAZIONE EVENTI DA DATABASE
        databaseReference.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeData(dataSnapshot);
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            public void onCancelled(DatabaseError databaseError) {
            }


        });

    }

    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.
        final int flag = 0;
        final DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        final DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        getDatabaseReferencevento = FirebaseDatabase.getInstance().getReference();

        databaseReferenceutente.child("UserID").child("Utenti").child(nome1).child("Eventi Creati").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                int var = 0;
                int flag = 0;
                for (DataSnapshot child : children) {
                    final String evento = child.getKey().toString();

                    if (evento.equals(doc.getId())){
                        for(DatabaseEvento eventi: eventi){
                            if(eventi.getId().equals(evento)){
                                flag = 1;
                            }
                        }
                        if(flag == 0){
                            DatabaseEvento.date_collection_arr.add(eve);
                            eventi.add(doc);
                        }
                    }

                }
                if(eventi.size() != 0){
                    nessunEve.setVisibility(View.GONE);

                }
                else{
                    nessunEve.setVisibility(View.VISIBLE);
                }

                adapter = new AdaptEventoAmm(getApplication(), eventi, itemClickListener);

                //listaEventiView.setAdapter(adapter);
                listaEventiView.setAdapter(new AdaptEventoAmm(getApplication(), eventi, new AdaptEventoAmm.OnItemClickListener() {
                    @Override public void onItemClick(DatabaseEvento item) {

                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mData = item.getDate();
                        String mImage = item.getImmagine();
                        String mId = item.getId();
                        String mDescription = item.getDescrizione();
                        Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliAmm.class);
                        intent.putExtra("titolo", mTitolo);
                        intent.putExtra("luogo", mLuogo);
                        intent.putExtra("data", mData);
                        intent.putExtra("immagine", mImage);
                        intent.putExtra("descrizione", mDescription);
                        intent.putExtra("id",mId);
                        startActivity(intent);
                    }


                }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento.date_collection_arr.remove(eve);
        eventi.remove(doc);

        adapter = new AdaptEventoAmm(getApplication(), eventi, itemClickListener);
        listaEventiView.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){

                case R.id.nav_profileAmm:
                    SharedPreferences.Editor editor2 = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor2.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor2.apply();
                    startActivity( new Intent(getApplicationContext(), AmmProfileFragment.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.nav_richieste:
                    startActivity( new Intent(getApplicationContext(), AmmHomeFragment.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.nav_add:
                    startActivity( new Intent(getApplicationContext(), AddEventoActivity.class));


            }
            return true;
        }
    };

    public void onBackPressed() {
        if (doubleTap) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AmmHomeFragment.this);
            builder.setTitle("");
            builder.setMessage("Uscire dall'applicazione?");
            builder.setCancelable(false);

            builder.setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    System.exit(0);
                    finish();


                }
            });
            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else{
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;

                }
            }, 1000);
        }
    }

}
