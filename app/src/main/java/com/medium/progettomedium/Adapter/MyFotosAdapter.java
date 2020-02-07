package com.medium.progettomedium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyFotosAdapter extends RecyclerView.Adapter<MyFotosAdapter.ImageViewHolder> {

    private Context mContext;
    private List<DatabaseEvento> mPosts;

    public MyFotosAdapter(Context context, List<DatabaseEvento> posts){
        mContext = context;
        mPosts = posts;
    }


    @NonNull
    @Override
    public MyFotosAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.addapt_evento, parent, false);
        return new MyFotosAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyFotosAdapter.ImageViewHolder holder, final int position) {

        //holder.bind(mPosts.get(position), listener);
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseEvento evento = mPosts.get(position);


    }





    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView titolo;
        private TextView luogo;
        private TextView data;
        private ImageView immagine;
        ImageView like;
        TextView likes;
        private Button stato;

        public ImageViewHolder(View itemView) {
            super(itemView);
            titolo = itemView.findViewById(R.id.titolo);
            luogo = itemView.findViewById(R.id.luogo);
            data = itemView.findViewById(R.id.data);
            immagine = itemView.findViewById(R.id.immagine);

            stato = itemView.findViewById(R.id.stato);
            // likes = itemView.findViewById(R.id.likes);
        }

        public void bind(final DatabaseEvento evento, final AdaptEvento.OnItemClickListener listener) {

            titolo.setText(evento.getTitolo());
            luogo.setText(evento.getLuogo());
            data.setText(evento.getDate());
            Picasso.get().load(evento.getImmagine()).into(immagine);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(evento);
                }
            });
            stato.setText(evento.getStato());
        }

    }
}