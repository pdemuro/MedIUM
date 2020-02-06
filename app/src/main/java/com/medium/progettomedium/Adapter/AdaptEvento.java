package com.medium.progettomedium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptEvento extends RecyclerView.Adapter<AdaptEvento.ViewHolder>{
    protected   Context c;
    List<DatabaseEvento> eventi;
    View mView;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    View v = mView;

    public interface OnItemClickListener{

        void onItemClick(DatabaseEvento evento);

    }

    private List<DatabaseEvento> listaEventi;
    private OnItemClickListener listener;


    public AdaptEvento(Context c, List<DatabaseEvento> listaEventi, OnItemClickListener listener) {
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
        holder.bind(listaEventi.get(position), listener);
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String nome1= firebaseUser.getDisplayName().replaceAll("%20" ," ");
        final DatabaseEvento evento = listaEventi.get(position);
        isLike(evento.getTitolo(),holder.like);
        //nrLikes(holder.likes,evento.getId());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti")
                            .child(nome1).child("Likes").child(evento.getTitolo()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti")
                            .child(nome1).child("Likes").child(evento.getTitolo()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titolo;
        private TextView luogo;
        private TextView data;
        private ImageView immagine;
        ImageView like;
        TextView likes;

        public ViewHolder(View itemView) {
            super(itemView);
            titolo = itemView.findViewById(R.id.titolo);
            luogo = itemView.findViewById(R.id.luogo);
            data = itemView.findViewById(R.id.data);
            immagine = itemView.findViewById(R.id.immagine);

            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
        }

        public void bind(final DatabaseEvento evento, final OnItemClickListener listener) {

            titolo.setText(evento.getTitolo());
            luogo.setText(evento.getLuogo());
            data.setText(evento.getDate());
            Picasso.get().load(evento.getImmagine()).into(immagine);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(evento);
                }
            });
        }
    }

    private void isLike(final String eventId, final ImageView imageView){
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       FirebaseUser nome = firebaseAuth.getCurrentUser();
        final String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti")
                .child(nome1).child("Likes");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(eventId).exists()){
                   // imageView.setImageResource(R.drawable.ic_liked);
                   // imageView.setImageResource(R.drawable.circlered);
                    imageView.setTag("Liked");

                }else{
                    //imageView.setImageResource(R.drawable.ic_favorite);
                   // imageView.setImageResource(R.drawable.circle100x100);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

