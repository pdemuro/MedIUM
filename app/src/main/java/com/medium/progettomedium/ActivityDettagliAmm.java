package com.medium.progettomedium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Adapter.AdaptAmm;
import com.medium.progettomedium.Adapter.AdaptAmmAccettati;
import com.medium.progettomedium.Fragment.AmmHomeFragment;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.view.GestureCropImageView;

import java.util.ArrayList;

public class ActivityDettagliAmm extends AppCompatActivity {

     RecyclerView recyclerView,elencoAccettati;
     Button accettati,prenotati;
     ConstraintLayout modifica,delete;
     AdaptAmm ammAdapter;
     AdaptAmmAccettati ammAdapter2;
     ArrayList<DatabaseUtente> utenti = new ArrayList<DatabaseUtente>();
     DatabaseReference databaseReferenceutente;
     DatabaseReference databaseReference;
     AdaptAmm.OnItemClickListener itemClickListener;
     AdaptAmmAccettati.OnItemClickListener itemClickListener2;
     ImageView close,immagine;
     TextView titolo,luogo,data;
     private GestureDetectorCompat mGestureDetector;
    private static final String TAG = "ActivityDettagliAmm";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_amm);


        final String title = getIntent().getStringExtra("titolo");
        final String place = getIntent().getStringExtra("luogo");
        final String description = getIntent().getStringExtra("descrizione");
        final String image = getIntent().getStringExtra("immagine");
        final String data1 = getIntent().getStringExtra("data");
        final String id = getIntent().getStringExtra("id");

        mGestureDetector= new GestureDetectorCompat(this, new GestureListener()) ;
        recyclerView = findViewById(R.id.elenco_richieste);

        accettati=findViewById(R.id.accettati);
        prenotati=findViewById(R.id.prenotati);
        close=findViewById(R.id.close);
        immagine= findViewById(R.id.immagine);
        delete=findViewById(R.id.cElimina);
        modifica= findViewById(R.id.cModifica);
        titolo=findViewById(R.id.titolo);
        luogo = findViewById(R.id.luogo);
        data= findViewById(R.id.data);

        titolo.setText(title);
        Picasso.get().load(image).into(immagine);
        luogo.setText(place);
        data.setText(data1);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDettagliAmm.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler eliminare definitivamente l'evento? ");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("Eventi").orderByChild("id").equalTo(id);

                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                                Toast.makeText(ActivityDettagliAmm.this,"Evento eliminiato",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ActivityDettagliAmm.this, AmmHomeFragment.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "Cancellato", databaseError.toException());
                            }
                        });

                    }
                });

                // On pressing the cancel button
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                // Showing Alert Message
                builder.show();



            }

        });

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ActivityDettagliAmm.this, ActivityModificaEvento.class);
                intent.putExtra("titolo", title);
                intent.putExtra("luogo", place);
                intent.putExtra("descrizione", description);
                intent.putExtra("immagine", image);
                intent.putExtra("data", data1);
                intent.putExtra("id", id);
                startActivity(intent);


            }
        });

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


                                ammAdapter2 = new AdaptAmmAccettati(ActivityDettagliAmm.this, utenti,id, itemClickListener2);

                                //listaEventiView.setAdapter(adapter);
                                elencoAccettati.setAdapter(new AdaptAmmAccettati(ActivityDettagliAmm.this, utenti,id, new AdaptAmmAccettati.OnItemClickListener() {
                                    @Override public void onItemClick(DatabaseUtente item) {

                                        String mNome = item.getNome()+" "+item.getCognome();
                                        String mMail = item.getMail();
                                        String mPhone = item.getPhone();
                                        String mId = item.getId();
                                        String mImage = item.getImageUrl();
                                        String mDescrizione = item.getDescrizione();
                                        String mCategoria = item.getCategory();
                                        Intent intent = new Intent(getApplicationContext(), ActivityVisualizzaUtente.class);
                                        intent.putExtra("nome", mNome);
                                        intent.putExtra("mail", mMail);
                                        intent.putExtra("phone", mPhone);
                                        intent.putExtra("id",mId);
                                        intent.putExtra("immagine",mImage);
                                        intent.putExtra("category",mCategoria);
                                        intent.putExtra("description",mDescrizione);
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
        /*  if(velocityX < 0) {
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

          }*/
            final int SWIPE_MIN_DISTANCE = 120;
            final int SWIPE_MAX_OFF_PATH = 250;
            final int SWIPE_THRESHOLD_VELOCITY = 200;
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    elencoAccettati.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    accettati.setBackgroundColor(Color.parseColor("#20444444"));
                    prenotati.setBackgroundColor(0xDCDCDC);
                    elencoAccetta();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    elencoAccettati.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    prenotati.setBackgroundColor(Color.parseColor("#20444444"));
                    accettati.setBackgroundColor(0xDCDCDC);
                    elencoRic();
                }
            } catch (Exception e) {
                // nothing
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


                                ammAdapter = new AdaptAmm(ActivityDettagliAmm.this, utenti,id, itemClickListener);

                                //listaEventiView.setAdapter(adapter);
                                recyclerView.setAdapter(new AdaptAmm(ActivityDettagliAmm.this, utenti,id, new AdaptAmm.OnItemClickListener() {
                                    @Override public void onItemClick(DatabaseUtente item) {

                                        String mNome = item.getNome()+" "+item.getCognome();
                                        String mMail = item.getMail();
                                        String mPhone = item.getPhone();
                                        String mId = item.getId();
                                        String mImage = item.getImageUrl();
                                        String mCategoria = item.getCategory();
                                        Intent intent = new Intent(getApplicationContext(), ActivityVisualizzaUtente.class);
                                        intent.putExtra("nome", mNome);
                                        intent.putExtra("mail", mMail);
                                        intent.putExtra("phone", mPhone);
                                        intent.putExtra("id",mId);
                                        intent.putExtra("immagine",mImage);
                                        intent.putExtra("category",mCategoria);
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
