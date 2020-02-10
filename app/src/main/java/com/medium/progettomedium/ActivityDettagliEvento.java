package com.medium.progettomedium;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ActivityDettagliEvento extends AppCompatActivity {


    TextView titolo, luogo, descrizione;
    ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_evento);
        titolo = findViewById(R.id.titolo_dettagli_evento);
        luogo = findViewById(R.id.luogo_dettagli_evento);
        descrizione = findViewById(R.id.textView2);
        foto = findViewById(R.id.foto_dettagli_evento);

        String title = getIntent().getStringExtra("title");
        String place = getIntent().getStringExtra("place");
        String description = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("image");

        titolo.setText(title);
        luogo.setText(place);
        descrizione.setText(description);
        Picasso.get().load(image).into(foto);
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
