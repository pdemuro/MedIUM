package com.medium.progettomedium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Adapter.AdaptAmm;
import com.medium.progettomedium.Adapter.AdaptEvento;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;

import java.util.ArrayList;

public class ActivityDettagliAmm extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptAmm ammAdapter;
    private ArrayList<DatabaseUtente> utenti = new ArrayList<DatabaseUtente>();
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference databaseReference;
    private AdaptAmm.OnItemClickListener itemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_amm);

        recyclerView = findViewById(R.id.elenco_richieste);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(ammAdapter);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        utenti = new ArrayList<DatabaseUtente>();

        String title = getIntent().getStringExtra("title");
        String place = getIntent().getStringExtra("description");
        String description = getIntent().getStringExtra("descrizione");
        String image = getIntent().getStringExtra("image");
        String data1 = getIntent().getStringExtra("date");
        final String id = getIntent().getStringExtra("id");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceutente.child("UserID").child("Utenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                int var = 0;
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
