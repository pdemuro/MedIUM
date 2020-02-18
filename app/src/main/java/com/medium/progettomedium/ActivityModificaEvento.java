package com.medium.progettomedium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ActivityModificaEvento extends AppCompatActivity {

    EditText titolo, descrizione;
    TextView luogo, data;
    ImageView foto;
    TextView salva;
    ImageView close;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_evento);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();

        titolo = findViewById(R.id.titolo_modifica_evento);
        luogo = findViewById(R.id.luogo_modifica_evento);
        descrizione = findViewById(R.id.descrizione_modifica_evento);
        foto = findViewById(R.id.foto_modifica_evento);
        data = findViewById(R.id.data_modifica_evento);
        salva = findViewById(R.id.save);
        close = findViewById(R.id.close);

        final String title = getIntent().getStringExtra("title");
        String place = getIntent().getStringExtra("description");
        String description = getIntent().getStringExtra("descrizione");
        String image = getIntent().getStringExtra("image");
        String data1 = getIntent().getStringExtra("date");
        final String id = getIntent().getStringExtra("id");

        titolo.setText(title);
        luogo.setText(place);
        descrizione.setText(description);
        data.setText(data1);
        Picasso.get().load(image).into(foto);

        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEvento(titolo.getText().toString(),descrizione.getText().toString());
            }


        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
    }
    private void updateEvento(String titolo, String descrizione){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Eventi").child(titolo);

        HashMap<String, Object> map = new HashMap<>();
        map.put("titolo", titolo);
        map.put("descrizione", descrizione);
        // map.put("bio", bio);

        reference.updateChildren(map);

        Toast.makeText(ActivityModificaEvento.this, "Informazioni Aggiornate!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("publisherid", firebaseUser.getUid());
        this.startActivity(intent);
        //startActivity(new Intent(this, MainActivity.class));
    }

}
