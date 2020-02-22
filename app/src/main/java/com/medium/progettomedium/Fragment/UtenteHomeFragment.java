package com.medium.progettomedium.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.Adapter.AdaptCalendario;
import com.medium.progettomedium.Adapter.AdaptEventoUtente;
import com.medium.progettomedium.Adapter.UserAdapter;
import com.medium.progettomedium.AddEventoActivity;
import com.medium.progettomedium.AddPostActivity;
import com.medium.progettomedium.GPSTracker;
import com.medium.progettomedium.MapActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class UtenteHomeFragment extends AppCompatActivity implements LocationListener {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView, recyclerView2;
    private Toolbar toolbar;
    boolean doubleTap = false;
    private ConstraintLayout icone, iconeFiltro;
    private AdaptEventoUtente eventAdapter;
    private List<DatabaseEvento> eventList;
    private List<DatabaseUtente> userList;
    private UserAdapter userAdapter;
    private ArrayList<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private AdaptEventoUtente adapter;
    private AdaptEventoUtente.OnItemClickListener itemClickListener;
    DatabaseReference mRef;
    private DatabaseReference databaseReference;
    ConstraintLayout menoDistante, calendario, mappa, questaSettimana;
    public AdaptCalendario adaptCalendario;
    EditText search_bar;
    ImageButton previous;
    TextView testo_mese, nomeFiltro, testoFiltroAttivo, menoDista;
    ImageButton next;
    LinearLayout layoutCalendario;
    RelativeLayout giorni;
    GridView gridview;
    LinearLayout filtroAttivo;
    public GregorianCalendar mese_calendario;
    Button close;
    public Double tvLongi;
    public Double tvLati;
    LocationManager locationManager;
    public int var = 0;
    Button btnShowLocation;
    ImageView icoSearch;
    ConstraintLayout cRicerca;

    // GPSTracker class
    GPSTracker gps;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference getDatabaseReferencevento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(UtenteHomeFragment.this));

        recyclerView2 =findViewById(R.id.recycler_view2);
         recyclerView2.setLayoutManager(new LinearLayoutManager(UtenteHomeFragment.this));

        search_bar = findViewById(R.id.search_bar);
        iconeFiltro = findViewById(R.id.IconsFiltro);
        close = findViewById(R.id.close);
        nomeFiltro = findViewById(R.id.nomeFiltro);
        icoSearch = findViewById(R.id.icoSearch);
        cRicerca = findViewById(R.id.cRicerca);

        userList = new ArrayList<>();
        recyclerView.setAdapter(eventAdapter);
        recyclerView2.setAdapter(eventAdapter);

        menoDistante = findViewById(R.id.buttonMenoDistante);
        questaSettimana = findViewById(R.id.buttonQuestaSettimana);
        calendario = findViewById(R.id.buttonCalendario);
        mappa = findViewById(R.id.buttonMap);
        icone = findViewById(R.id.IconsId);
        //filtroAttivo=view.findViewById(R.id.filtroAttivo);
        //testoFiltroAttivo=view.findViewById(R.id.textFiltroAttivo);
        testo_mese = findViewById(R.id.tv_month);
        previous = (ImageButton) findViewById(R.id.ib_prev);
        layoutCalendario = findViewById(R.id.ll_calendar);
        giorni = findViewById(R.id.giorni);
        gridview = (GridView) findViewById(R.id.gv_calendar);
        next = (ImageButton)findViewById(R.id.Ib_next);
        //readUsers();
        // readEvent();
        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        Event();
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();


        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");

        icoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             cRicerca.setVisibility(View.VISIBLE);
             icoSearch.setVisibility(View.GONE);
            }
        });
        //POPOLAZIONE EVENTI DA DATABASE
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icone.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.GONE);
                layoutCalendario.setVisibility(View.GONE);
                giorni.setVisibility(View.GONE);

                // layoutCalendario.setVisibility(View.GONE);
                //filtroAttivo.setVisibility(View.GONE);
                giorni.setVisibility(View.GONE);
            }
        });
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //searchUsers(charSequence.toString().toLowerCase());
                firebaseSearch(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconeFiltro.setVisibility(View.GONE);
                icone.setVisibility(View.VISIBLE);
                layoutCalendario.setVisibility(View.GONE);
                giorni.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Event();

            }
        });

        mappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckPermission()) {

                    icone.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    recyclerView2.setVisibility(View.GONE);

                    layoutCalendario.setVisibility(View.GONE);
                    giorni.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                }

            }
        });

        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nomeFiltro.setText("Calendario");
                iconeFiltro.setVisibility(View.VISIBLE);
                layoutCalendario.setVisibility(View.VISIBLE);
                giorni.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.GONE);
                icone.setVisibility(View.INVISIBLE);

                Calendario();
            }
        });
        questaSettimana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeFiltro.setText("Recente");
                iconeFiltro.setVisibility(View.VISIBLE);
                icone.setVisibility(View.INVISIBLE);
                eventisettimana(eventi);
            }
        });

        menoDistante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create class object
                gps = new GPSTracker(UtenteHomeFragment.this);

                // Check if GPS enabled
                if (gps.canGetLocation()) {

                    final double latitudePos = gps.getLatitude();
                    final double longitudePos = gps.getLongitude();
                    iconeFiltro.setVisibility(View.VISIBLE);
                    icone.setVisibility(View.INVISIBLE);
                    nomeFiltro.setText("Vicino a te");

                    compare(tvLongi, tvLati, eventi);
                    var=0;
                    // \n is for new line
                   // Toast.makeText(getContext(), "Your Location is - \nLat: " + latitudePos + "\nLong: " + longitudePos, Toast.LENGTH_LONG).show();
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });

    }

    private void Event(){


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

    }
    private void firebaseSearch(String searchText) {


        final String query = searchText.toLowerCase();


        mRef = FirebaseDatabase.getInstance().getReference().child("Eventi");

        // Query firebaseSearchQuery = mRef.orderByChild("luogo").startAt(query).endAt(query + "\uf0ff");

        ValueEventListener firebaseSearchQuery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    eventi.clear();
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        String titolo = dss.child("titolo").getValue(String.class);
                        String luogo = dss.child("luogo").getValue(String.class);
                        if (luogo.contains(query) || titolo.contains(query)) {
                            final DatabaseEvento databaseEvento = dss.getValue(DatabaseEvento.class);
                            eventi.add(databaseEvento);
                        }
                    }
                }
                adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);

                //listaEventiView.setAdapter(adapter);
                recyclerView2.setVisibility(View.GONE);
                recyclerView.setAdapter(new AdaptEventoUtente(getApplicationContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
                    @Override
                    public void onItemClick(DatabaseEvento item) {


                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mDescrizione = item.getDescrizione();
                        String mImage = item.getImmagine();
                        String mData = item.getDate();
                        String mId = item.getId();
                        Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                        intent.putExtra("title", mTitolo);
                        intent.putExtra("description", mLuogo);
                        intent.putExtra("descrizione", mDescrizione);
                        intent.putExtra("image", mImage);
                        intent.putExtra("date", mData);
                        intent.putExtra("id", mId);
                        startActivity(intent);
                    }

                }));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };

        mRef.addListenerForSingleValueEvent(firebaseSearchQuery);
        eventi.clear();
    }

    private void searchUsers(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseUtente user = snapshot.getValue(DatabaseUtente.class);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DatabaseUtente databaseUtente = snapshot.getValue(DatabaseUtente.class);

                        userList.add(databaseUtente);

                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.


        int flag = 0;
        final DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        final DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);

        for (DatabaseEvento eventi : eventi) {
            if (eventi.getTitolo().equals(doc.getTitolo())) {
                flag = 1;
            }
        }
        if (flag == 0) {
            DatabaseEvento.date_collection_arr.add(eve);
            eventi.add(doc);
        }
        adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);

        //listaEventiView.setAdapter(adapter);
        recyclerView.setAdapter(new AdaptEventoUtente(getApplicationContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
            @Override
            public void onItemClick(DatabaseEvento item) {


                String mTitolo = item.getTitolo();
                String mLuogo = item.getLuogo();
                String mDescrizione = item.getDescrizione();
                String mImage = item.getImmagine();
                String mData = item.getDate();
                String mId = item.getId();
                Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                intent.putExtra("title", mTitolo);
                intent.putExtra("description", mLuogo);
                intent.putExtra("descrizione", mDescrizione);
                intent.putExtra("image", mImage);
                intent.putExtra("date", mData);
                intent.putExtra("id", mId);
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

        adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);
        recyclerView.setAdapter(adapter);
    }
   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/


    protected void setNextMonth() {
        if (mese_calendario.get(GregorianCalendar.MONTH) == mese_calendario.getActualMaximum(GregorianCalendar.MONTH)) {
            mese_calendario.set((mese_calendario.get(GregorianCalendar.YEAR) + 1), mese_calendario.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            mese_calendario.set(GregorianCalendar.MONTH,
                    mese_calendario.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (mese_calendario.get(GregorianCalendar.MONTH) == mese_calendario.getActualMinimum(GregorianCalendar.MONTH)) {
            mese_calendario.set((mese_calendario.get(GregorianCalendar.YEAR) - 1), mese_calendario.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mese_calendario.set(GregorianCalendar.MONTH, mese_calendario.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public void refreshCalendar() {
        adaptCalendario.refreshDays();
        adaptCalendario.notifyDataSetChanged();
        testo_mese.setText(android.text.format.DateFormat.format("MMMM yyyy", mese_calendario));
    }

    public void Calendario() {
        mese_calendario = (GregorianCalendar) GregorianCalendar.getInstance();
        //mese_calendario_copia = (GregorianCalendar) mese_calendario.clone();
        adaptCalendario = new AdaptCalendario(getApplicationContext(), mese_calendario, DatabaseEvento.date_collection_arr);

        testo_mese.setText(android.text.format.DateFormat.format("MMMM yyyy", mese_calendario));

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
            }
        });
        gridview.setAdapter(adaptCalendario);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedGridDate = AdaptCalendario.day_string.get(position);
                getPositionList(selectedGridDate, getApplicationContext());
            }

        });

        setNextMonth();

        setPreviousMonth();

        refreshCalendar();


    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext().getApplicationContext(), ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UtenteHomeFragment.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 101);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(var==0) {
            tvLongi = location.getLongitude();
            tvLati = location.getLatitude();
            var = 1;

            //Toast.makeText(getContext(), tvLongi + " " + tvLati, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    public void eventisettimana(ArrayList<DatabaseEvento> lista1) {

        final Date currentTime = Calendar.getInstance().getTime();
        String tempo = currentTime.toString().substring(8, 10);

        final int lunghmese = YearMonth.now()
                .lengthOfMonth();
        String mese = currentTime.toString().substring(4, 7);
        int valmese = 0;
        if (mese.equals("Jan")) {
            valmese = 1;
        } else if (mese.equals("Feb")) {
            valmese = 2;
        } else if (mese.equals("Mar")) {
            valmese = 3;
        } else if (mese.equals("Apr")) {
            valmese = 4;
        } else if (mese.equals("May")) {
            valmese = 5;
        } else if (mese.equals("Jun")) {
            valmese = 6;
        } else if (mese.equals("Jul")) {
            valmese = 7;
        } else if (mese.equals("Aug")) {
            valmese = 8;
        } else if (mese.equals("Sep")) {
            valmese = 9;
        } else if (mese.equals("Oct")) {
            valmese = 10;
        } else if (mese.equals("Nov")) {
            valmese = 11;
        } else if (mese.equals("Dec")) {
            valmese = 12;
        }
        final int ggmese = valmese;
        final int gsetti = Integer.parseInt(tempo);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Query reference = FirebaseDatabase.getInstance().getReference("Eventi").orderByChild("date");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventi.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dataD = snapshot.child("date").getValue(String.class);
                    String tempo2 = dataD.substring(0, 2);
                    String mese2 = dataD.substring(3, 5);
                    int valmese2 = Integer.parseInt(mese2);
                    int gsetti2 = Integer.parseInt(tempo2);

                    if (ggmese == valmese2) {
                        if ((gsetti2 - gsetti) <= 7 && (gsetti2 - gsetti) >= 0) {
                            //ouble distanceToPlace1 = distance(latitude, longitude, latCurrent1, longCurrent1);

                            DatabaseEvento databaseEvento = snapshot.getValue(DatabaseEvento.class);
                            eventi.add(databaseEvento);
                        }
                    } else if ((lunghmese - gsetti) < 7) {
                        if ((gsetti2 < (7 - (lunghmese - gsetti)))) {
                            DatabaseEvento databaseEvento = snapshot.getValue(DatabaseEvento.class);
                            eventi.add(databaseEvento);
                        }
                    }

                }
                Collections.reverse(eventi);
                adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);
                recyclerView.setAdapter(new AdaptEventoUtente(getApplicationContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
                    @Override
                    public void onItemClick(DatabaseEvento item) {


                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mDescrizione = item.getDescrizione();
                        String mImage = item.getImmagine();
                        String mData = item.getDate();
                        String mId = item.getId();
                        Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                        intent.putExtra("title", mTitolo);
                        intent.putExtra("description", mLuogo);
                        intent.putExtra("descrizione", mDescrizione);
                        intent.putExtra("image", mImage);
                        intent.putExtra("date", mData);
                        intent.putExtra("id", mId);
                        startActivity(intent);
                    }


                }));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //adapter.notifyDataSetChanged();


    }


    public void compare(final Double latCurrent1, final Double longCurrent1, ArrayList<DatabaseEvento> lista1) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Eventi");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (latCurrent1 != null && longCurrent1 != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        double latitude = snapshot.child("latitude").getValue(Double.class);
                        double longitude = snapshot.child("longitude").getValue(Double.class);
                        double distanceToPlace1 = distance2(latitude, longitude, latCurrent1, longCurrent1);


                        snapshot.getRef().child("distanza").setValue(distanceToPlace1);

                        DatabaseEvento databaseEvento = snapshot.getValue(DatabaseEvento.class);
                        databaseEvento.setDistanza(distanceToPlace1);


                    }
                }

                Query query = FirebaseDatabase.getInstance().getReference("Eventi").orderByChild("distanza");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        eventi.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DatabaseEvento evento = snapshot.getValue(DatabaseEvento.class);
                            eventi.add(evento);

                        }
                        Collections.reverse(eventi);
                        adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);
                        recyclerView.setAdapter(new AdaptEventoUtente(getApplicationContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
                            @Override
                            public void onItemClick(DatabaseEvento item) {


                                String mTitolo = item.getTitolo();
                                String mLuogo = item.getLuogo();
                                String mDescrizione = item.getDescrizione();
                                String mImage = item.getImmagine();
                                String mData = item.getDate();
                                String mId = item.getId();
                                Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                                intent.putExtra("title", mTitolo);
                                intent.putExtra("description", mLuogo);
                                intent.putExtra("descrizione", mDescrizione);
                                intent.putExtra("image", mImage);
                                intent.putExtra("date", mData);
                                intent.putExtra("id", mId);
                                startActivity(intent);
                            }


                        }));
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin(Math.sqrt(
                Math.pow(sin(deltaLat / 2), 2) +
                        cos(fromLat) * cos(toLat) *
                                Math.pow(sin(deltaLon / 2), 2)));
        return radius * angle;
    }

    public void getPositionList(String date, final Context act) {

        int len = DatabaseEvento.date_collection_arr.size();
        JSONArray jbarrays = new JSONArray();

        DatabaseEvento evento = new DatabaseEvento();
        eventi.clear();
        for (int j = 0; j < len; j++) {
            if (DatabaseEvento.date_collection_arr.get(j).date.equals(date) && !DatabaseEvento.date_collection_arr.get(j).id.equals(evento.id)) {
              /*  HashMap<String, String> maplist = new HashMap<String, String>();
                maplist.put("hnames", DatabaseEvento.date_collection_arr.get(j).date);
                maplist.put("hsubject", DatabaseEvento.date_collection_arr.get(j).titolo);
                maplist.put("descript", DatabaseEvento.date_collection_arr.get(j).luogo);
                JSONObject json1 = new JSONObject(maplist);
                jbarrays.put(json1);*/
                evento = DatabaseEvento.date_collection_arr.get(j);

                eventi.add(DatabaseEvento.date_collection_arr.get(j));
            }
        }

        adapter = new AdaptEventoUtente(getApplicationContext(), eventi, itemClickListener);
        recyclerView2.setVisibility(View.VISIBLE);
        //listaEventiView.setAdapter(adapter);
        recyclerView2.setAdapter(new AdaptEventoUtente(getApplicationContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
            @Override
            public void onItemClick(DatabaseEvento item) {


                String mTitolo = item.getTitolo();
                String mLuogo = item.getLuogo();
                String mDescrizione = item.getDescrizione();
                String mImage = item.getImmagine();
                String mData = item.getDate();
                String mId = item.getId();
                Intent intent = new Intent(getApplicationContext(), ActivityDettagliEvento.class);
                intent.putExtra("title", mTitolo);
                intent.putExtra("description", mLuogo);
                intent.putExtra("descrizione", mDescrizione);
                intent.putExtra("image", mImage);
                intent.putExtra("date", mData);
                intent.putExtra("id", mId);
                startActivity(intent);
            }


        }));



            /*final Dialog dialogs = new Dialog(context);
            dialogs.setContentView(R.layout.dialog_prenotazione);
            listTeachers = (ListView) dialogs.findViewById(R.id.list_teachers);
            ImageView imgCross = (ImageView) dialogs.findViewById(R.id.img_cross);
            listTeachers.setAdapter(new AdaptEventoPrenotabile(context, getMatchList(jbarrays + "")));
            imgCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogs.dismiss();
                }
            });
            dialogs.show();

        }*/


    }
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        // earth radius is in mile
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(lngDiff / 2)
                * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; int meterConversion = 1609;
        double kmConvertion = 1.6093; // return
        new Float(distance * meterConversion).floatValue();
        return new Float(distance * kmConvertion).floatValue() ; // return String.format("%.2f", distance)+" m";
    }
    private static double distance_in_meter(final double lat1, final double lon1, final double lat2, final double lon2) {
        float pk = (float) (180/3.14169);
        double a1 = lat1 / pk;
        double a2 = lon1 / pk;
        double b1 = lat2 / pk;
        double b2 = lon2 / pk;

        double t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2);
        double t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2);
        double t3 = sin(a1) * sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

    private double distance2(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)); dist = Math.acos(dist);
                dist = rad2deg(dist); dist = dist * 60 * 1.1515; return (dist); } private double deg2rad(double deg) { return (deg * Math.PI / 180.0); } private double rad2deg(double rad) { return (rad * 180.0 / Math.PI); }

   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/
   private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

           switch (menuItem.getItemId()){
               case R.id.nav_home:
                   startActivity( new Intent(getApplicationContext(), UtenteHomeFragment.class));
                   break;
               case R.id.nav_addpost:
                   startActivity( new Intent(getApplicationContext(), AddPostActivity.class));
                   break;

               case R.id.nav_profile:
                   SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                   editor.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                   editor.apply();
                   startActivity( new Intent(getApplicationContext(), ProfileFragment.class));
                   break;
               case R.id.nav_profileAmm:
                   SharedPreferences.Editor editor2 = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                   editor2.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                   editor2.apply();
                   startActivity( new Intent(getApplicationContext(), AmmProfileFragment.class));
                   break;
               case R.id.nav_richieste:
                   startActivity( new Intent(getApplicationContext(), AmmHomeFragment.class));
                   break;
               case R.id.nav_add:
                   startActivity( new Intent(getApplicationContext(), AddEventoActivity.class));


           }
           return true;
       }
   };

    public void onBackPressed() {
        if (doubleTap) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UtenteHomeFragment.this);
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
                    startActivity(new Intent(UtenteHomeFragment.this,UtenteHomeFragment.class));
                }
            }, 500);
        }
    }
}
