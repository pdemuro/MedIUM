package com.medium.progettomedium.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptEventoUtente extends RecyclerView.Adapter<AdaptEventoUtente.ViewHolder>{
    private    Context c;
    List<DatabaseEvento> eventi;
    View mView;

    View v = mView;

    public interface OnItemClickListener{

        void onItemClick(DatabaseEvento evento);

    }

    private List<DatabaseEvento> listaEventi;
    private OnItemClickListener listener;


    public AdaptEventoUtente(Context c, List<DatabaseEvento> listaEventi, OnItemClickListener listener) {
        this.c = c;
        this.listaEventi = listaEventi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addapt_evento,parent,false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(listaEventi.get(position),c, listener);
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String nome1= firebaseUser.getDisplayName().replaceAll("%20" ," ");
        final DatabaseEvento evento = listaEventi.get(position);
    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titolo;
        private TextView luogo;
        private TextView data;
        private ImageView immagine;
        private Button stato;
        private Button modifica;
        private DatabaseEvento evento;
        private DatabaseReference databaseReference;
        private DatabaseReference databaseReference2;
        private DatabaseReference databaseReferenceutente;
        private FirebaseAuth firebaseAuth;

        public ViewHolder(View itemView) {
            super(itemView);
            titolo = itemView.findViewById(R.id.titolo);
            luogo = itemView.findViewById(R.id.luogo);
            data = itemView.findViewById(R.id.data);
            immagine = itemView.findViewById(R.id.immagine);
            stato = itemView.findViewById(R.id.stato);
            stato.setVisibility(View.VISIBLE);
            modifica = itemView.findViewById(R.id.modifica);

        }

        public void bind(final DatabaseEvento evento,Context c, final OnItemClickListener listener) {
            final String id = evento.getId();
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            final String nome1= user.getDisplayName().replaceAll("%20" ," ");
            databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference2 = FirebaseDatabase.getInstance().getReference();
            final Context context = c;
            titolo.setText(evento.getTitolo());
            luogo.setText(evento.getLuogo());
            data.setText(evento.getDate());
            Picasso.get().load(evento.getImmagine()).into(immagine);

            databaseReferenceutente.child("UserID").child("Utenti").child(nome1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    // shake hands with each of them.'
                    int var = 0;

                    for (DataSnapshot child : children) {
                        if(child.getValue().equals("Utente")) {
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
                                        if(id.equals(child.getKey())) {
                                            var =1;
                                            String stato = child.getValue().toString();
                                            if(stato.equals("3")){
                                                var = 2;
                                            }else if(stato.equals("4")){
                                                var=3;
                                            }
                                        }
                                    }
                                    if(var == 2){
                                        stato.setEnabled(false);

                                        stato.setText("Prenotato");
                                        stato.setTextColor(Color.parseColor("#000000"));
                                        stato.setBackgroundResource(R.drawable.gradius_prenotato);
                                    } else if(var == 1){

                                        stato.setEnabled(false);

                                        stato.setText("In attesa");
                                        stato.setTextColor(Color.parseColor("#000000"));
                                        stato.setBackgroundResource(R.drawable.gradius_attesa);
                                    }else if(var == 3){

                                        stato.setEnabled(false);
                                        stato.setText("Rifiutato");
                                        stato.setTextColor(Color.parseColor("#000000"));
                                        stato.setBackgroundResource(R.drawable.gradius_rifiutato);
                                    }
                                    else{
                                        stato.setText("Prenotabile");

                                        stato.setTextColor(Color.parseColor("#000000"));
                                        stato.setBackgroundResource(R.drawable.gradius_image);
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




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(evento);

                }
            });


        }


    }





}

