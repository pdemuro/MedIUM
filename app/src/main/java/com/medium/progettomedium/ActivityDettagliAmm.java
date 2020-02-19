package com.medium.progettomedium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Adapter.AdaptAmm;
import com.medium.progettomedium.Adapter.AdaptAmmAccettati;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.yalantis.ucrop.view.GestureCropImageView;

import java.util.ArrayList;

public class ActivityDettagliAmm extends AppCompatActivity {

     RecyclerView recyclerView,elencoAccettati;
     Button accettati,prenotati;
     AdaptAmm ammAdapter;
     AdaptAmmAccettati ammAdapter2;
     ArrayList<DatabaseUtente> utenti = new ArrayList<DatabaseUtente>();
     DatabaseReference databaseReferenceutente;
     DatabaseReference databaseReference;
     AdaptAmm.OnItemClickListener itemClickListener;
     AdaptAmmAccettati.OnItemClickListener itemClickListener2;
     ImageView close;
     private GestureDetectorCompat mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_amm);


        String title = getIntent().getStringExtra("title");
        String place = getIntent().getStringExtra("description");
        String description = getIntent().getStringExtra("descrizione");
        String image = getIntent().getStringExtra("image");
        String data1 = getIntent().getStringExtra("date");
        final String id = getIntent().getStringExtra("id");

        mGestureDetector= new GestureDetectorCompat(this, new GestureListener()) ;

        recyclerView = findViewById(R.id.elenco_richieste);

        accettati=findViewById(R.id.accettati);
        prenotati=findViewById(R.id.prenotati);
        close=findViewById(R.id.close);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        elencoAccettati=findViewById(R.id.elencoAccettati);
        elencoAccettati.setHasFixedSize(true);
        elencoAccettati.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(ammAdapter);
        elencoAccettati.setAdapter(ammAdapter2);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        utenti = new ArrayList<DatabaseUtente>();

        elencoAccettati.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        elencoAccettati.setLayoutManager(linearLayoutManager1);
        utenti = new ArrayList<DatabaseUtente>();
        elencoRic();

        prenotati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elencoAccettati.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                prenotati.setBackgroundColor(Color.parseColor("#20444444"));
                accettati.setBackgroundColor(0xDCDCDC);
                elencoRic();


            }
        });
        accettati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elencoAccettati.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                accettati.setBackgroundColor(Color.parseColor("#20444444"));
                prenotati.setBackgroundColor(0xDCDCDC);
                elencoAccetta();


            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
    public void elencoAccetta(){
        final String id = getIntent().getStringExtra("id");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceutente.child("UserID").child("Utenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                int var = 0;
                utenti.clear();
                for (DataSnapshot child : children) {
                    final DatabaseUtente utente = child.getValue(DatabaseUtente.class);
                    if(utente.getCategory().equals("Utente")) {
                        databaseReference.child("UserID").child("Utenti").child(utente.nome+" "+utente.cognome).child("prenotazioni").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get all of the children at this level.
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                // shake hands with each of them.'

                                for (DataSnapshot child : children) {
                                    if(id.equals(child.getKey())) {
                                        String stato = child.getValue().toString();
                                        if(stato.equals("3")){
                                            utenti.add(utente);
                                        }
                                    }

                                }


                                ammAdapter2 = new AdaptAmmAccettati(getApplication(), utenti,id, itemClickListener2);

                                //listaEventiView.setAdapter(adapter);
                                elencoAccettati.setAdapter(new AdaptAmmAccettati(getApplication(), utenti,id, new AdaptAmmAccettati.OnItemClickListener() {
                                    @Override public void onItemClick(DatabaseUtente item) {

                                        String mNome = item.getNome()+" "+item.getCognome();
                                        String mMail = item.getMail();
                                        String mPhone = item.getPhone();
                                        Intent intent = new Intent(elencoAccettati.getContext(), ActivityDettagliAmm.class);
                                        intent.putExtra("nome", mNome);
                                        intent.putExtra("mail", mMail);
                                        intent.putExtra("phone", mPhone);
                                        startActivity(intent);
                                    }


                                }));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private class GestureListener  extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
          if(velocityX < 0) {
              elencoAccettati.setVisibility(View.VISIBLE);
              recyclerView.setVisibility(View.GONE);
              accettati.setBackgroundColor(Color.parseColor("#20444444"));
              prenotati.setBackgroundColor(0xDCDCDC);
              elencoAccetta();
          }else{
              elencoAccettati.setVisibility(View.GONE);
              recyclerView.setVisibility(View.VISIBLE);
              prenotati.setBackgroundColor(Color.parseColor("#20444444"));
              accettati.setBackgroundColor(0xDCDCDC);
              elencoRic();

          }
           // Toast.makeText(ActivityDettagliAmm.this,"%f" ,velocityX,Toast.LENGTH_SHORT).show();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
    public boolean onTouchEvent(MotionEvent event){
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    public void elencoRic(){
        final String id = getIntent().getStringExtra("id");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceutente.child("UserID").child("Utenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                int var = 0;
                utenti.clear();
                for (DataSnapshot child : children) {
                    final DatabaseUtente utente = child.getValue(DatabaseUtente.class);
                    if(utente.getCategory().equals("Utente")) {
                        databaseReference.child("UserID").child("Utenti").child(utente.nome+" "+utente.cognome).child("prenotazioni").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get all of the children at this level.
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                // shake hands with each of them.'

                                for (DataSnapshot child : children) {
                                    if(id.equals(child.getKey())) {
                                        String stato = child.getValue().toString();
                                        if(stato.equals("2")){
                                            utenti.add(utente);
                                        }
                                    }

                                }


                                ammAdapter = new AdaptAmm(getApplication(), utenti,id, itemClickListener);

                                //listaEventiView.setAdapter(adapter);
                                recyclerView.setAdapter(new AdaptAmm(getApplication(), utenti,id, new AdaptAmm.OnItemClickListener() {
                                    @Override public void onItemClick(DatabaseUtente item) {

                                        String mNome = item.getNome()+" "+item.getCognome();
                                        String mMail = item.getMail();
                                        String mPhone = item.getPhone();
                                        Intent intent = new Intent(recyclerView.getContext(), ActivityDettagliAmm.class);
                                        intent.putExtra("nome", mNome);
                                        intent.putExtra("mail", mMail);
                                        intent.putExtra("phone", mPhone);
                                        startActivity(intent);
                                    }


                                }));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
