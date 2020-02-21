package com.medium.progettomedium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Adapter.AdaptEventoAmm;
import com.medium.progettomedium.Adapter.AdaptEventoModificabile;
import com.medium.progettomedium.Adapter.AdaptEventoUtente;
import com.medium.progettomedium.Adapter.MyFotosAdapter;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.Model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityVisualizzaAmm extends AppCompatActivity {

    ImageView vu_img_profilo,close;
    TextView vu_nome_utente, vu_categoria,email2;
    private RecyclerView vu_lista_foto;
    private List<Post> postList;
    private MyFotosAdapter myFotosAdapter;
    private List<DatabaseEvento> postList_saves;
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;
    private AdaptEventoAmm.OnItemClickListener itemClickListenerutente;
    private AdaptEventoAmm adapterutente;

    private RecyclerView listaEventiView;

    private List<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_amm);

        vu_img_profilo = findViewById(R.id.vu_img_profilo);
        vu_nome_utente = findViewById(R.id.vu_nome_utente);
        vu_categoria = findViewById(R.id.vu_categoria);
        email2=findViewById(R.id.email);

        listaEventiView = (RecyclerView) findViewById(R.id.vu_lista_foto);
     listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityVisualizzaAmm.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();
        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();
        close=findViewById(R.id.close);

        final String nome = getIntent().getStringExtra("nome");
        String immagine = getIntent().getStringExtra("immagine");
        String categoria = getIntent().getStringExtra("category");
        String email = getIntent().getStringExtra("mail");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vu_nome_utente.setText(nome);
        vu_categoria.setText(categoria);
       email2.setText(email);

        if (immagine != null) {
            Glide.with(ActivityVisualizzaAmm.this).load(immagine).into(vu_img_profilo);

        }
        myFotos();




    }
    public void myFotos(){

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
        final String nome = getIntent().getStringExtra("nome");

        final DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        final DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        databaseReference3 = FirebaseDatabase.getInstance().getReference();

                    databaseReference3.child("UserID").child("Utenti").child(nome).child("Eventi Creati").addValueEventListener(new ValueEventListener() {

                        /**
                         * This method will be invoked any time the data on the database changes.
                         * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get all of the children at this level.
                            DatabaseUtente databaseUtente = dataSnapshot.getValue(DatabaseUtente.class);

                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            // shake hands with each of them.'
                            int var = 0;
                            for (DataSnapshot child : children) {

                                if (doc.getId().equals(child.getKey())) {
                                    var = 1;
                                }

                            }
                            if (var == 1) {
                                DatabaseEvento.date_collection_arr.add(eve);
                                eventi.add(doc);
                            }
                             listaEventiView.setAdapter(new AdaptEventoUtente(ActivityVisualizzaAmm.this, eventi, new AdaptEventoUtente.OnItemClickListener() {
                                @Override
                                public void onItemClick(DatabaseEvento item) {


                                    String mTitolo = item.getTitolo();
                                    String mLuogo = item.getLuogo();
                                    String mDescrizione = item.getDescrizione();
                                    String mImage = item.getImmagine();
                                    String mData = item.getDate();
                                    String mId = item.getId();
                                    Intent intent = new Intent(ActivityVisualizzaAmm.this, ActivityDettagliEvento.class);
                                    intent.putExtra("title", mTitolo);
                                    intent.putExtra("description", mLuogo);
                                    intent.putExtra("descrizione", mDescrizione);
                                    intent.putExtra("image", mImage);
                                    intent.putExtra("date", mData);
                                    intent.putExtra("id", mId);
                                    startActivity(intent);
                                }

                            }));
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





        eventi.clear();
    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

    }
}
