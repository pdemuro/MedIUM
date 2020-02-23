package com.medium.progettomedium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.Fragment.AmmHomeFragment;
import com.medium.progettomedium.Fragment.UtenteHomeFragment;
import com.medium.progettomedium.Model.DatabaseUtente;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            FirebaseUser nome = firebaseAuth.getCurrentUser();
            String nome1 = nome.getDisplayName().replaceAll("%20", " ");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (getApplicationContext() == null) {
                        return;
                    }
                    DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                    if (user.category.equals("Utente")) {
                        startActivity(new Intent(StartActivity.this, UtenteHomeFragment.class));
                    } else {
                        startActivity(new Intent(StartActivity.this, AmmHomeFragment.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }
    }
}
