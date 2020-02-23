package com.medium.progettomedium.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.ActivityDettagliAmm;
import com.medium.progettomedium.AddEventoActivity2;
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
        ImageView immagine;

        public ViewHolder(View itemView) {
            super(itemView);
            nome_utente = itemView.findViewById(R.id.nome_utente);
            mail = itemView.findViewById(R.id.mail);
            telefono = itemView.findViewById(R.id.telefono);
            accetta = itemView.findViewById(R.id.accetta);
            rifiuta = itemView.findViewById(R.id.rifiuta);
            immagine = itemView.findViewById(R.id.immagine);


        }

        public void bind(final DatabaseUtente utente, final String idEvento, final OnItemClickListener listener) {
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
            Picasso.get().load(utente.getImageUrl()).into(immagine);




            accetta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AlertDialogStyle);
                    // Setting Dialog Title
                    //builder.setTitle("Internet non disponibile");

                    // Setting Dialog Message
                    builder.setMessage("Accettare questo utente? Hai visualizzato il suo profilo?");

                    // On pressing the Settings button.
                    builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            databaseReference.child("UserID").child("Utenti").child(nome).child("prenotazioni").child(eventoid).setValue(3);
                            databaseReference2.child("Eventi").child(idEvento).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    DatabaseEvento databaseEvento = dataSnapshot.getValue(DatabaseEvento.class);
                                    Intent intent = new Intent(c, ActivityDettagliAmm.class);
                                    intent.putExtra("titolo", databaseEvento.getTitolo());
                                    intent.putExtra("luogo", databaseEvento.getLuogo());
                                    intent.putExtra("descrizione", databaseEvento.getDescrizione());
                                    intent.putExtra("immagine",databaseEvento.getImmagine());
                                    intent.putExtra("data",databaseEvento.getDate());
                                    intent.putExtra("id",databaseEvento.getId());
                                    c.startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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


                    //Intent intent = new Intent(c, AmmHomeFragment.class);
                    //c.startActivity(intent);
                }

            });
            rifiuta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AlertDialogStyle);
                    // Setting Dialog Title
                    //builder.setTitle("Internet non disponibile");

                    // Setting Dialog Message
                    builder.setMessage("Rifiutare questo utente? Hai visualizzato il suo profilo?");

                    // On pressing the Settings button.
                    builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            databaseReference.child("UserID").child("Utenti").child(nome).child("prenotazioni").child(eventoid).setValue(4);
                            Toast.makeText(c,"Operazione avvenuta con successo",Toast.LENGTH_SHORT).show();
                            databaseReference2.child("Eventi").child(idEvento).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    DatabaseEvento databaseEvento = dataSnapshot.getValue(DatabaseEvento.class);
                                    Intent intent = new Intent(c, ActivityDettagliAmm.class);
                                    intent.putExtra("titolo", databaseEvento.getTitolo());
                                    intent.putExtra("luogo", databaseEvento.getLuogo());
                                    intent.putExtra("descrizione", databaseEvento.getDescrizione());
                                    intent.putExtra("immagine",databaseEvento.getImmagine());
                                    intent.putExtra("data",databaseEvento.getDate());
                                    intent.putExtra("id",databaseEvento.getId());
                                    c.startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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

                    //Intent intent = new Intent(c, AmmHomeFragment.class);
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
