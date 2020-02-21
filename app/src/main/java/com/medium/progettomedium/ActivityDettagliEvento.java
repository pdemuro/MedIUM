package com.medium.progettomedium;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Fragment.UtenteHomeFragment;
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



        final String title = getIntent().getStringExtra("title");
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
                for (final DataSnapshot child : children) {
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
                                            prenota.setText("In Attesa");
                                           // prenota.setTextColor(Color.parseColor("#ffffbc00"));
                                            prenota.setBackgroundResource(R.drawable.gradius_attesa);
                                        }else if(stato.equals("3")){
                                            prenota.setText("Prenotato");
                                          //  prenota.setTextColor(Color.parseColor("#ffc6ff00"));
                                            prenota.setBackgroundResource(R.drawable.gradius_prenotato);
                                        }else if(stato.equals("4")){
                                            prenota.setText("Rifiutato");
                                          //  prenota.setTextColor(Color.parseColor("#ffff1744"));
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

        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                final String nome1= firebaseUser.getDisplayName().replaceAll("%20" ," ");
                databaseReference.child("UserID").child("Utenti").child(nome1).child("prenotazioni").addValueEventListener(new ValueEventListener() {

                    /**
                     * This method will be invoked any time the data on the database changes.
                     * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        // shake hands with each of them.'
                        int var = 0;
                        for (DataSnapshot child : children) {
                            String ciao=child.getKey();
                            if(id.equals(ciao)) {
                                var =1;
                            }
                        }
                        if(var == 1){
                            prenota.setEnabled(false);

                        }
                        else{

                            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                            final String nome1= firebaseUser.getDisplayName().replaceAll("%20" ," ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDettagliEvento.this);
                            builder.setTitle("Conferma Prenotazione");
                            builder.setMessage("Vuoi confermare la prenotazione per questo evento?");
                            builder.setCancelable(false);

                            builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti")
                                            .child(nome1).child("prenotazioni").child(id).setValue(2);

                                    prenota.setEnabled(false);

                                    prenota.setText("In attesa");
                                    prenota.setTextColor(Color.parseColor("#ffffbc00"));
                                    prenota .setBackgroundResource(R.drawable.gradius_attesa);
                                    startActivity(getIntent());

                                }
                            });
                            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();



                                /*FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti")
                                        .child(nome1).child("prenotazioni").child(evento.getId()).setValue(2);
                                stato.setEnabled(false);

                                stato.setText("In attesa");
                                stato.setBackgroundColor(Color.YELLOW);*/
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
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