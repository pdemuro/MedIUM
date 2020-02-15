package com.medium.progettomedium.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.ActivityDettagliAmm;
import com.medium.progettomedium.Fragment.HomeFragment;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptAmm extends RecyclerView.Adapter<AdaptAmm.ViewHolder>{
    private static Context c;
    List<DatabaseUtente> utenti;
    View mView;
    String idEvento;

    View v = mView;

    public interface OnItemClickListener{

        void onItemClick(DatabaseUtente utente);

    }

    private List<DatabaseUtente> listaUtenti;
    private OnItemClickListener listener;

    public String getIdEvento() {
        return idEvento;
    }

    public AdaptAmm(Context c, List<DatabaseUtente> listaUtenti, String idEvento, OnItemClickListener listener) {
        this.c = c;
        this.listaUtenti = listaUtenti;
        this.listener = listener;
        this.idEvento = idEvento;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addapt_amm,parent,false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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

        public ViewHolder(View itemView) {
            super(itemView);
            nome_utente = itemView.findViewById(R.id.nome_utente);
            mail = itemView.findViewById(R.id.mail);
            telefono = itemView.findViewById(R.id.telefono);
            accetta = itemView.findViewById(R.id.accetta);
            rifiuta = itemView.findViewById(R.id.rifiuta);

        }

        public void bind(final DatabaseUtente utente,String idEvento, final OnItemClickListener listener) {
            final String eventoid = idEvento;
            final String id = utente.getId();
            firebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser user = firebaseAuth.getCurrentUser();
            final String nome1= user.getDisplayName().replaceAll("%20" ," ");
            databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference2 = FirebaseDatabase.getInstance().getReference();

            final String nome = utente.getFullname();
            nome_utente.setText(utente.getFullname());
            mail.setText(utente.getMail());
            telefono.setText(utente.getPhone());
            //Picasso.get().load(evento.getImmagine()).into(immagine); VALUTARE SE INSERIRE

            accetta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    databaseReference.child("UserID").child("Utenti").child(nome).child("prenotazioni").child(eventoid).setValue(3);
                    //Intent intent = new Intent(c, HomeFragment.class);
                    //c.startActivity(intent);
                }

            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(utente);

                }
            });


        }


    }





}
