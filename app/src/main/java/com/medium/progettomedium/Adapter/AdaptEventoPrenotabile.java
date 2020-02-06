package com.medium.progettomedium.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.medium.progettomedium.EventoPrenotabile;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class AdaptEventoPrenotabile extends BaseAdapter {


    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private FirebaseAuth firebaseAuth;
    protected Context c;
    private ArrayList<EventoPrenotabile> alCustom;
    private String sturl;


    public AdaptEventoPrenotabile(Context c, ArrayList<EventoPrenotabile> alCustom) {
        this.c = c;
        this.alCustom = alCustom;

    }

    @Override
    public int getCount() {
        return alCustom.size();

    }

    @Override
    public Object getItem(int i) {
        return alCustom.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final View listViewItem = inflater.inflate(R.layout.addapt_prenotazione, null, true);

            TextView tvSubject = (TextView) listViewItem.findViewById(R.id.tv_type);
            final TextView tvDescription = (TextView) listViewItem.findViewById(R.id.tv_class);
            final Button prenota = (Button) listViewItem.findViewById(R.id.prenota);

            final FirebaseUser user = firebaseAuth.getCurrentUser();

            databaseReference2.child("UserID").child("Utenti").addValueEventListener(new ValueEventListener() {

                /**
                 * * This method will be invoked any time the data on the database changes.
                 * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                 *
                 * @param dataSnapshot
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get all of the children at this level.
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    // shake hands with each of them.'
                    for (DataSnapshot child : children) {
                        String email = (String) dataSnapshot.getValue().toString().trim();
                        if (email.contains(user.getEmail())) {
                            prenota.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        databaseReference2.child("UserID").child("Organizzatori").addValueEventListener(new ValueEventListener() {

            /**
             * * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    String email = (String) dataSnapshot.getValue().toString().trim();
                    if (email.contains(user.getEmail())) {
                        prenota.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Eventi").child(alCustom.get(position).getLuogo()).child("prenotazioni").addValueEventListener(new ValueEventListener() {

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
                for (DataSnapshot child : children) {
                    String name = (String) dataSnapshot.getValue().toString().trim();
                    if(name.contains(user.getUid())){

                        prenota.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
            prenota.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    databaseReference.child("Eventi").child(alCustom.get(position).getLuogo()).child("prenotazioni").child(user.getDisplayName()).setValue(user.getUid());
                    DatabaseEvento.date_collection_arr=new ArrayList<DatabaseEvento>();
                }


            });

            tvSubject.setText(alCustom.get(position).getLuogo());
            tvDescription.setText(alCustom.get(position).getDescrizione());

        return  listViewItem;
    }




}

