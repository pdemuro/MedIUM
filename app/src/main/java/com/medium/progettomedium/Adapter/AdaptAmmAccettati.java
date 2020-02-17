package com.medium.progettomedium.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptAmmAccettati extends RecyclerView.Adapter<AdaptAmmAccettati.ViewHolder>{
    private static Context c;
    List<DatabaseUtente> utenti;
    View mView;
    String idEvento;

    View v = mView;

    public interface OnItemClickListener{

        void onItemClick(DatabaseUtente utente);

    }

    private List<DatabaseUtente> listaUtenti;
    private AdaptAmmAccettati.OnItemClickListener listener;

    public String getIdEvento() {
        return idEvento;
    }

    public AdaptAmmAccettati(Context c, List<DatabaseUtente> listaUtenti, String idEvento, AdaptAmmAccettati.OnItemClickListener listener) {
        this.c = c;
        this.listaUtenti = listaUtenti;
        this.listener = listener;
        this.idEvento = idEvento;
    }

    @NonNull
    @Override
    public AdaptAmmAccettati.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapt_amm_accettati,parent,false);


        return new AdaptAmmAccettati.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptAmmAccettati.ViewHolder holder, int position) {
        holder.bind(listaUtenti.get(position),getIdEvento(), listener);
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String nome1= firebaseUser.getDisplayName().replaceAll("%20" ," ");
        final DatabaseUtente utente = listaUtenti.get(position);
    }

    @Override
    public int getItemCount() {
        return listaUtenti.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nome_utente;
        private TextView mail;
        private TextView telefono;
        private Button accetta;
        private Button rifiuta;
        private DatabaseUtente utente;
        private DatabaseReference databaseReference;
        private DatabaseReference databaseReference2;
        private DatabaseReference databaseReferenceutente;
        private FirebaseAuth firebaseAuth;
        ImageView immagine;

        public ViewHolder(View itemView) {
            super(itemView);
            nome_utente = itemView.findViewById(R.id.nome_utente);
            mail = itemView.findViewById(R.id.mail);
            telefono = itemView.findViewById(R.id.telefono);

            immagine=itemView.findViewById(R.id.immagine);

        }

        public void bind(final DatabaseUtente utente,String idEvento, final AdaptAmmAccettati.OnItemClickListener listener) {
            final String eventoid = idEvento;
            final String id = utente.getId();
            firebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser user = firebaseAuth.getCurrentUser();
            final String nome1= user.getDisplayName().replaceAll("%20" ," ");
            databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference2 = FirebaseDatabase.getInstance().getReference();

            final String nome = utente.getNome()+" "+utente.getCognome();
            nome_utente.setText(nome);
            mail.setText(utente.getMail());
            telefono.setText(utente.getPhone());
            Picasso.get().load(utente.getImageUrl()).into(immagine);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(utente);

                }
            });


        }


    }





}