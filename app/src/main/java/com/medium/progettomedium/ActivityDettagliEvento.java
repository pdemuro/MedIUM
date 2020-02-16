package com.medium.progettomedium;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.squareup.picasso.Picasso;

public class ActivityDettagliEvento extends AppCompatActivity {


    TextView titolo, luogo, descrizione,data;
    ImageView foto;
    Button prenota;
    ImageView close;

    private DatabaseReference databaseReferenceutente;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_evento);
        titolo = findViewById(R.id.titolo_dettagli_evento);
        luogo = findViewById(R.id.luogo_dettagli_evento);
        descrizione = findViewById(R.id.descrizione_dettagli_evento);
        foto = findViewById(R.id.foto_dettagli_evento);
        data = findViewById(R.id.data_dettagli_evento);
        prenota = findViewById(R.id.button);
        close = findViewById(R.id.close);

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
                        databaseReference.child("UserID").child("Utenti").child(utente.fullname).child("prenotazioni").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get all of the children at this level.
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                // shake hands with each of them.'

                                for (DataSnapshot child : children) {
                                    if(id.equals(child.getKey())) {
                                        String stato = child.getValue().toString();

                                        if(stato.equals("1")){
                                            prenota.setText("Prenota Ora");
                                            prenota.setBackgroundResource(R.drawable.gradius_image);
                                        }
                                        if(stato.equals("2")){
                                            prenota.setText("In Attesa");
                                            prenota.setTextColor(Color.parseColor("#ffffbc00"));
                                            prenota.setBackgroundResource(R.drawable.gradius_attesa);
                                        }
                                        if(stato.equals("3")){
                                            prenota.setText("Prenotato");
                                            prenota.setTextColor(Color.parseColor("#ffc6ff00"));
                                            prenota.setBackgroundResource(R.drawable.gradius_prenotato);
                                        }
                                        if(stato.equals("4")){
                                            prenota.setText("Rifiutato");
                                            prenota.setTextColor(Color.parseColor("#ffff1744"));
                                            prenota.setBackgroundResource(R.drawable.gradius_rifiutato);
                                        }
                                    }

                                }

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
        titolo.setText(title);
        luogo.setText(place);
        descrizione.setText(description);
        data.setText(data1);
        Picasso.get().load(image).into(foto);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
