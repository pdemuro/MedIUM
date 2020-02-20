package com.medium.progettomedium;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;

public class AddEventoActivity2 extends AppCompatActivity {


    private DatabaseReference databaseReference;
    public String luogo;
    private Button getPlace;
    public Double latitude;
    public Double longitude;
    int PLACE_PICKER_REQUEST = 1;
    private TextView boxData;
    private Uri mImageUri;
    private String data2;
    private String date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    Button buttonAvanti,buttonAnnulla;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_event2);

        final String titolo = getIntent().getStringExtra("titolo");
       final  String descrizione = getIntent().getStringExtra("descrizione");
        final String data = getIntent().getStringExtra("data");
        final Double latitudine =getIntent().getDoubleExtra("latitudine",0.0);
        final Double longitude = getIntent().getDoubleExtra("longitudine",0.0);
        final String luogo = getIntent().getStringExtra("luogo");

        final String immagine = getIntent().getStringExtra("immagine");

        databaseReference = FirebaseDatabase.getInstance().getReference(); //carica database
        boxData = (TextView) findViewById(R.id.editDate);
        buttonAvanti =  findViewById(R.id.buttonAvanti);
        getPlace =  findViewById(R.id.getPlace);
        buttonAnnulla = (Button) findViewById(R.id.buttonIndietro);

        if(data != null){
            boxData.setText(data);
        }
        if(luogo != null){
            getPlace.setText(luogo);
        }


        buttonAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String titolo = getIntent().getStringExtra("titolo");
                String descrizione = getIntent().getStringExtra("descrizione");

                Intent intent = new Intent(AddEventoActivity2.this, AddEventoActivity.class);
                intent.putExtra("titolo", titolo);
                intent.putExtra("descrizione", descrizione);
                intent.putExtra("data", date);
                intent.putExtra("latitudine", latitudine);
                intent.putExtra("longitudine", longitude);
                intent.putExtra("luogo", luogo);
                intent.putExtra("immagine", immagine);
                startActivity(intent);
                finish();
            }
        });
        boxData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEventoActivity2.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: dd/mm/yyyy: " + month + "-" + day + "-" + year);

                if (month > 9 && day > 9) {
                    date = day + "-" + month + "-" + year;
                    data2 = date;
                } else if (month > 9 && day < 10) {

                    date = day + "-" + month + "-" + year;
                    data2 = "0" + day + "-" + month + "-" + year;
                } else if (month < 10 && day > 9) {
                    date = day + "-" + month + "-" + year;
                    data2 = day + "-" + "0" + month + "-" + year;
                } else {
                    date = day + "-" + month + "-" + year;
                    data2 = "0" + day + "-" + "0" + month + "-" + year;
                }

                boxData.setText(date);


            }
        };
        getPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(AddEventoActivity2.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String titolo = getIntent().getStringExtra("titolo");
                String descrizione = getIntent().getStringExtra("descrizione");

                if (boxData != null) {
                    Intent intent = new Intent(AddEventoActivity2.this, AddEventoActivity3.class);
                    intent.putExtra("titolo", titolo);
                    intent.putExtra("descrizione", descrizione);
                    intent.putExtra("data", date);
                    intent.putExtra("latitudine", latitude);
                    intent.putExtra("longitudine", longitude);
                    intent.putExtra("luogo", luogo);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        if (requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(AddEventoActivity2.this, data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo = (String) place.getName();

            getPlace.setText(luogo);

        }
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
