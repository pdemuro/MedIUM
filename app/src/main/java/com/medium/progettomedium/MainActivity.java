package com.medium.progettomedium;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.medium.progettomedium.Fragment.AddEventFragment;
import com.medium.progettomedium.Fragment.FavouriteFragment;
import com.medium.progettomedium.Fragment.HomeFragment;
import com.medium.progettomedium.Fragment.ProfileFragment;
import com.medium.progettomedium.Fragment.SearchFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FirebaseUser nome = FirebaseAuth.getInstance().getCurrentUser();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

                    Bundle intent = getIntent().getExtras();
                    if (intent != null){
                        String publisher = intent.getString("publisherid");

                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", publisher);
                        editor.apply();

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        bottomNavigationView.getMenu().getItem(4).setChecked(true);

                    } else {
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment()).commit();

                    }
                }
                else{
                    bottomNavigationViewOrga = findViewById(R.id.bottom_navigation_amm);
                    bottomNavigationViewOrga.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    bottomNavigationViewOrga.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

                    Bundle intent = getIntent().getExtras();
                    if (intent != null){
                        String publisher = intent.getString("publisherid");

                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", publisher);
                        editor.apply();

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                        bottomNavigationViewOrga.getMenu().getItem(2).setChecked(true);

                    } else {
                        bottomNavigationViewOrga.getMenu().getItem(0).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment()).commit();

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, OptionsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    break;
                case R.id.nav_addpost:
                    //startActivity( new Intent(MainActivity.this, MapActivity.class));
                    break;
                case R.id.nav_search:

                    selectedFragment= new SearchFragment();
                    break;

                case R.id.nav_favorite:

                    selectedFragment= new FavouriteFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment= new ProfileFragment();
                    break;
                case R.id.nav_richieste:
                    selectedFragment= new HomeFragment();
                    break;
                case R.id.nav_add:
                    selectedFragment= new AddEventFragment();
            }
            if(selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            }

            return true;
        }
    };



    @Override
    public void onBackPressed() {
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
            }, 1000);*/
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}
