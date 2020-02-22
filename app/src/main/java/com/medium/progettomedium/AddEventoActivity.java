package com.medium.progettomedium;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medium.progettomedium.Fragment.AmmHomeFragment;

public class AddEventoActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    ImageView close, image_profile, exit, save;
    TextView tv_change;
    private EditText editTitolo;
    private EditText editDescrizione;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    private GestureDetectorCompat mGestureDetector;
    private ImageView buttonAnnulla;
        TextView buttonAvanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_event);

        close = findViewById(R.id.close);
        save = findViewById(R.id.save);
        exit = findViewById(R.id.exit);

        final String titolo = getIntent().getStringExtra("titolo");
        final  String descrizione = getIntent().getStringExtra("descrizione");
        final String data = getIntent().getStringExtra("data");
        final Double latitudine =getIntent().getDoubleExtra("latitudine",0.0);
        final Double longitudine = getIntent().getDoubleExtra("longitudine",0.0);
        final String luogo = getIntent().getStringExtra("luogo");
        final String immagine = getIntent().getStringExtra("immagine");

        databaseReference = FirebaseDatabase.getInstance().getReference(); //carica database
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editDescrizione = (EditText) findViewById(R.id.editDescrizione);

        if(titolo != null){
            editTitolo.setText(titolo);
        }
        if(descrizione != null){
            editDescrizione.setText(descrizione);
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventoActivity.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler uscire dalla creazione dell'evento? I dati inseriti andranno persi");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        startActivity(new Intent(AddEventoActivity.this, AmmHomeFragment.class));
                        finish();

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

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! editTitolo.getText().toString().equals("") &&! editDescrizione.getText().toString().equals("")){
                    String titolo = editTitolo.getText().toString().trim();
                    String descrizione = editDescrizione.getText().toString().trim();

                    Intent intent = new Intent(AddEventoActivity.this, AddEventoActivity2.class);

                    intent.putExtra("titolo", titolo);
                    intent.putExtra("descrizione", descrizione);
                    intent.putExtra("data", data);
                    intent.putExtra("latitudine", latitudine);
                    intent.putExtra("longitudine", longitudine);
                    intent.putExtra("luogo", luogo);
                    intent.putExtra("immagine", immagine);
                    startActivity(intent);
                    finish();
                }else if( editTitolo.getText().toString().equals("") && editDescrizione.getText().toString().equals("")){
                    editTitolo.setError("Non hai inserito nessun titolo");
                    editDescrizione.setError("Non hai inserito nessuna descrizione");
                }
                else if(editTitolo.getText().toString().equals("")){
                   // Toast.makeText(AddEventoActivity.this,"Non hai inserito nessun titolo",Toast.LENGTH_SHORT).show();
                    editTitolo.setError("Non hai inserito nessun titolo");

                } else if(editDescrizione.getText().toString().equals("")){
                    editDescrizione.setError("Non hai inserito nessuna descrizione");
                    //Toast.makeText(AddEventoActivity.this,"Non hai inserito nessuna descrizione",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
 /*   final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    final int SWIPE_MIN_DISTANCE = 120;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;
                    String titolo ="";
                    String descrizione="";
                    try {
                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                            return false;
                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && titolo != null && descrizione != null) {
                            titolo = editTitolo.getText().toString().trim();
                            descrizione = editDescrizione.getText().toString().trim();

                            Intent intent = new Intent(getContext(), AddEventoFragment2.class);
                            intent.putExtra("title", titolo);
                            intent.putExtra("description", descrizione);
                            startActivity(intent);

                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        }
                    } catch (Exception e) {
                        // nothing
                    }
                    // Toast.makeText(ActivityDettagliAmm.this,"%f" ,velocityX,Toast.LENGTH_SHORT).show();
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


    });
*/

}
