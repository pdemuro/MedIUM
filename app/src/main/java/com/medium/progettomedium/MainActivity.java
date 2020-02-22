package com.medium.progettomedium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.medium.progettomedium.Fragment.AmmHomeFragment;
import com.medium.progettomedium.Fragment.AmmProfileFragment;
import com.medium.progettomedium.Fragment.ProfileFragment;
import com.medium.progettomedium.Fragment.UtenteHomeFragment;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    BottomNavigationView bottomNavigationViewUtente;
    BottomNavigationView bottomNavigationViewOrga;
    Toolbar mTopToolbar;
    Fragment selectedFragment = null;
    boolean doubleTap = false;
    FirebaseAuth firebaseAuth;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);



        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").child(nome1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                if(user.category.equals("Utente")){
                    bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    bottomNavigationView.setVisibility(View.VISIBLE);


                    Bundle intent = getIntent().getExtras();
                    if (intent != null){
                        String publisher = intent.getString("publisherid");

                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", publisher);
                        editor.apply();

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);

                    } else {
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new UtenteHomeFragment()).commit();

                    }
                }
                else{
                    bottomNavigationViewOrga = findViewById(R.id.bottom_navigation_amm);
                    bottomNavigationViewOrga.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    bottomNavigationViewOrga.setVisibility(View.VISIBLE);


                    Bundle intent = getIntent().getExtras();


                    if (intent != null ){

                        String publisher = intent.getString("publisherid");

                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", publisher);
                        editor.apply();

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        bottomNavigationViewOrga.getMenu().getItem(2).setChecked(true);

                    }else{
                        bottomNavigationViewOrga.getMenu().getItem(0).setChecked(true);
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                                new AmmHomeFragment()).commit();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        

        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment= new UtenteHomeFragment();
                    break;
                case R.id.nav_addpost:
                    startActivity( new Intent(MainActivity.this, AddPostActivity.class));
                    break;

                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment= new ProfileFragment();
                    break;
                    case R.id.nav_profileAmm:
                    SharedPreferences.Editor editor2 = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor2.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor2.apply();
                    selectedFragment= new AmmProfileFragment();
                    break;
                case R.id.nav_richieste:
                    selectedFragment= new AmmHomeFragment();
                    break;
                case R.id.nav_add:
                    startActivity( new Intent(MainActivity.this, AddEventoActivity.class));


            }
            if(selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            }

            return true;
        }
    };




    /*public void onBackPressed() {
        if (doubleTap) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {/*

            Toast.makeText(this, "Premi indietro di nuovo per uscire dall'applicazione!", Toast.LENGTH_SHORT).show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 1000);
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }*/


    public void onBackPressed() {
        if (doubleTap) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("");
            builder.setMessage("Uscire dall'applicazione?");
            builder.setCancelable(false);

            builder.setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    System.exit(0);
                    finish();


                }
            });
            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else{
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                    FirebaseUser nome = firebaseAuth.getCurrentUser();
                    String nome1= nome.getDisplayName().replaceAll("%20" ," ");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").child(nome1);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                            if (user.category.equals("Utente")) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new UtenteHomeFragment()).commit();
                            }
                            else  if (user.category.equals("Organizzatore")){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new AmmHomeFragment()).commit();
                            }
                        }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AmmHomeFragment()).commit();
                }
            }, 500);
        }
    }

}
