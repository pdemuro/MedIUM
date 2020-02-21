package com.medium.progettomedium;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ActivityModificaEvento extends AppCompatActivity {

    MaterialEditText titoloE, descrizione;

    public String luogo2;
    private String data2;
    TextView luogo, data;
    ImageView foto;
    TextView salva;
    ImageView close;
    ConstraintLayout posizione;
    ConstraintLayout dataC;
    public Double latitude;
    public Double longitude;
    int PLACE_PICKER_REQUEST = 1;
    String idEvento;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private Uri mImageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_evento);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference("immaginiEventi");

        titoloE = findViewById(R.id.titolo_modifica_evento);
        luogo = findViewById(R.id.luogo_modifica_evento);
        descrizione = findViewById(R.id.descrizione_modifica_evento);
        foto = findViewById(R.id.foto_modifica_evento);
        data = findViewById(R.id.data_modifica_evento);
        salva = findViewById(R.id.save);
        close = findViewById(R.id.close);
        posizione = findViewById(R.id.posizione);
        dataC = findViewById(R.id.data);

        String title = getIntent().getStringExtra("titolo");
        String place = getIntent().getStringExtra("luogo");
        String description = getIntent().getStringExtra("descrizione");
        String image = getIntent().getStringExtra("immagine");
        String data1 = getIntent().getStringExtra("data");
        final String id = getIntent().getStringExtra("id");

        idEvento=id;
        titoloE.setText(title);
        luogo.setText(place);
        descrizione.setText(description);
        data.setText(data1);
        Picasso.get().load(image).into(foto);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(230,180)
                        .start(ActivityModificaEvento.this);
            }
        });

        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEvento(id, titoloE.getText().toString(),descrizione.getText().toString());
            }


        });
        dataC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ActivityModificaEvento.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: dd/mm/yyyy: " + month + "-" + day + "-" + year);
                String date;
                if(month>9 && day > 9) {
                    date = day + "-" + month + "-" + year;
                    data2 = date;
                }else if(month > 9 && day < 10){

                    date = day + "-" + month + "-" + year;
                    data2 = "0"+day + "-" + month + "-" + year;
                }
                else if(month < 10 && day > 9){
                    date = day + "-" + month + "-" + year;
                    data2 = day + "-" + "0"+ month + "-" + year;
                }else{
                    date = day + "-" + month + "-" + year;
                    data2 = "0"+day + "-" + "0"+ month + "-" + year;
                }

                data.setText(date);


            }
        };
        posizione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try{
                    startActivityForResult(builder.build(ActivityModificaEvento.this), PLACE_PICKER_REQUEST);
                }
                catch(GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                }
                catch (GooglePlayServicesNotAvailableException e ){
                    e.printStackTrace();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
    }
    private void updateEvento(String idE,final String titolo,final String descrizione){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Eventi").child(idE);

        final HashMap<String, Object> map = new HashMap<>();
        map.put("titolo",titolo);
        map.put("descrizione",descrizione);
        if(data2 != null){
            map.put("date",data2);
        }

        if(luogo2 != null) {
            map.put("luogo", luogo2.toLowerCase());
        }
        //CARICAMENTO EVENTO
        if (mImageUri != null) {
            final StorageReference ref = FirebaseStorage.getInstance().getReference("immaginiEventi/" + System.currentTimeMillis() + ".jpg");

            uploadTask = ref.putFile(mImageUri); //VIENE INSERITO IL FILE NELLO STORAGE
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult(); //LINK SFONDO
                        map.put("immagine",downloadUri.toString());
                        reference.updateChildren(map);
                        Toast.makeText(ActivityModificaEvento.this, "Informazioni Aggiornate!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("publisherid", firebaseUser.getUid());
                        startActivity(intent);

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

        reference.updateChildren(map);
        Toast.makeText(ActivityModificaEvento.this, "Informazioni Aggiornate!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("publisherid", firebaseUser.getUid());
        this.startActivity(intent);

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(getApplicationContext(), data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo2 = (String) place.getName();
            luogo.setText(luogo2);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Picasso.get().load(mImageUri).into(foto);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
