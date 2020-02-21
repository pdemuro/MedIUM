package com.medium.progettomedium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Adapter.MyFotosAdapter;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityVisualizzaUtente extends AppCompatActivity {

    ImageView vu_img_profilo;
    TextView vu_nome_utente, vu_categoria;
    private RecyclerView vu_lista_foto;
    private List<Post> postList;
    private MyFotosAdapter myFotosAdapter;
    private List<DatabaseEvento> postList_saves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_utente);

        vu_img_profilo = findViewById(R.id.vu_img_profilo);
        vu_nome_utente = findViewById(R.id.vu_nome_utente);
        vu_categoria = findViewById(R.id.vu_categoria);
       vu_lista_foto = findViewById(R.id.vu_lista_foto);
        vu_lista_foto.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
       vu_lista_foto.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
       myFotosAdapter = new MyFotosAdapter(getApplicationContext(), postList);
        vu_lista_foto.setAdapter(myFotosAdapter);
        postList_saves = new ArrayList<>();

        String nome = getIntent().getStringExtra("nome");
        String immagine = getIntent().getStringExtra("immagine");
        String categoria = getIntent().getStringExtra("category");


        vu_nome_utente.setText(nome);
        vu_categoria.setText(categoria);
        if (immagine != null) {
            Glide.with(ActivityVisualizzaUtente.this).load(immagine).into(vu_img_profilo);

        }
        myFotos(nome);




    }
    private void myFotos(final String nome){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(nome)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
