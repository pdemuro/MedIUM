package com.medium.progettomedium.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.Adapter.AdaptEvento;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class HomeFragment extends Fragment{
    boolean doubleTap = false;
    private ArrayList<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private AdaptEvento adapter;
    private AdaptEvento.OnItemClickListener itemClickListener;
    private RecyclerView listaEventiView;
    DatabaseReference mRef;
    private DatabaseReference databaseReference;
    private Button statoprenotazione;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        listaEventiView = (RecyclerView) view.findViewById(R.id.row_adda);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();

        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");

        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

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

        return view;

    }


    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.
        int flag = 0;
        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        for(DatabaseEvento eventi: eventi){
            if(eventi.getTitolo().equals(doc.getTitolo())){
                flag = 1;
            }
        }
        if(flag == 0) {
            DatabaseEvento.date_collection_arr.add(eve);
            eventi.add(doc);
        }
        adapter = new AdaptEvento(getContext(), eventi, itemClickListener);

        //listaEventiView.setAdapter(adapter);
        listaEventiView.setAdapter(new AdaptEvento(getContext(), eventi, new AdaptEvento.OnItemClickListener() {
            @Override public void onItemClick(DatabaseEvento item) {

                String mTitolo = item.getTitolo();
                String mLuogo = item.getLuogo();
                String mData = item.getDate();
                String mImage = item.getImmagine();
                String mDescription = item.getImmagine();
                Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliEvento.class);
                intent.putExtra("titolo", mTitolo);
                intent.putExtra("luogo", mLuogo);
                intent.putExtra("data", mData);
                intent.putExtra("immagine", mImage);
                intent.putExtra("descrizione", mDescription);
                startActivity(intent);
            }


        }));
    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento.date_collection_arr.remove(eve);
        eventi.remove(doc);

        adapter = new AdaptEvento(getContext(), eventi, itemClickListener);
        listaEventiView.setAdapter(adapter);
    }
}
