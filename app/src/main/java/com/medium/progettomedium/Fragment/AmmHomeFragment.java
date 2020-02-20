package com.medium.progettomedium.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.ActivityDettagliAmm;
import com.medium.progettomedium.Adapter.AdaptEventoAmm;
import com.medium.progettomedium.MainActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

public class AmmHomeFragment extends Fragment{
    boolean doubleTap = false;
    private ArrayList<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private AdaptEventoAmm adapter;
    private AdaptEventoAmm.OnItemClickListener itemClickListener;
    private RecyclerView listaEventiView;
    DatabaseReference mRef;
    private DatabaseReference databaseReference;
    private Button statoprenotazione;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference getDatabaseReferencevento;
    private GestureDetectorCompat mGestureDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //mGestureDetector= new GestureDetectorCompat(getContext().getApplicationContext(), new GestureListener()) ;

        View view =inflater.inflate(R.layout.fragment_home, container, false);
        listaEventiView = (RecyclerView) view.findViewById(R.id.row_adda);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();

        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");
       /* final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        Toast.makeText(getContext(),"ciao",Toast.LENGTH_SHORT).show();
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {


                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                String var= "add";
                                Intent intent = new Intent(getContext(), ActivityDettagliAmm.class);
                                intent.putExtra("var", var);
                                startActivity(intent);                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // return gesture.onTouchEvent(event);

                gesture.onTouchEvent(event);
                return true; // <-- this line made the difference
            }
        });*/

//here the rest of your code

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
/*
    private class GestureListener  extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(velocityX < 0) {
                Toast.makeText(getContext(),"ciao",Toast.LENGTH_SHORT).show();
            }else{


            }
            // Toast.makeText(ActivityDettagliAmm.this,"%f" ,velocityX,Toast.LENGTH_SHORT).show();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        mGestureDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);
        return true; // <-- this line made the difference
    }*/
    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.
        final int flag = 0;
        final DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        final DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        getDatabaseReferencevento = FirebaseDatabase.getInstance().getReference();

        databaseReferenceutente.child("UserID").child("Utenti").child(nome1).child("Eventi Creati").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                int var = 0;
                int flag = 0;
                for (DataSnapshot child : children) {
                    final String evento = child.getKey().toString();

                    if (evento.equals(doc.getId())){
                        for(DatabaseEvento eventi: eventi){
                            if(eventi.getId().equals(evento)){
                                flag = 1;
                            }
                        }
                        if(flag == 0){
                            DatabaseEvento.date_collection_arr.add(eve);
                            eventi.add(doc);
                        }
                    }

                }
                adapter = new AdaptEventoAmm(getContext(), eventi, itemClickListener);

                //listaEventiView.setAdapter(adapter);
                listaEventiView.setAdapter(new AdaptEventoAmm(getContext(), eventi, new AdaptEventoAmm.OnItemClickListener() {
                    @Override public void onItemClick(DatabaseEvento item) {

                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mData = item.getDate();
                        String mImage = item.getImmagine();
                        String mId = item.getId();
                        String mDescription = item.getImmagine();
                        Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliAmm.class);
                        intent.putExtra("titolo", mTitolo);
                        intent.putExtra("luogo", mLuogo);
                        intent.putExtra("data", mData);
                        intent.putExtra("immagine", mImage);
                        intent.putExtra("descrizione", mDescription);
                        intent.putExtra("id",mId);
                        startActivity(intent);
                    }


                }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento.date_collection_arr.remove(eve);
        eventi.remove(doc);

        adapter = new AdaptEventoAmm(getContext(), eventi, itemClickListener);
        listaEventiView.setAdapter(adapter);
    }

}
