package com.medium.progettomedium;


import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.medium.progettomedium.EditProfileActivity;
import com.medium.progettomedium.Fragment.AmmHomeFragment;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class AddEventoActivity3 extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ImageView scegliSfondo,exit;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private UploadTask mUploadTask;
    private static final String TAG = "ActivityAdmin";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;
    ImageView close, image_profile, save;
    TextView tv_change;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_event3);

        close = findViewById(R.id.close);
        save = findViewById(R.id.save);
        exit = findViewById(R.id.exit);

        final String titolo = getIntent().getStringExtra("titolo");
        final String descrizione = getIntent().getStringExtra("descrizione");
        final String data = getIntent().getStringExtra("data");
        final Double latitudine =getIntent().getDoubleExtra("latitudine",0.0);
        final Double longitude = getIntent().getDoubleExtra("longitudine",0.0);
        final String luogo = getIntent().getStringExtra("luogo");
        final String immagine = getIntent().getStringExtra("immagine");





        //Slidr.attach(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference(); //carica database


        scegliSfondo = findViewById(R.id.addPost);

        mStorageRef = FirebaseStorage.getInstance().getReference(); //caricamento file dal database
/*
        buttonEvent.setOnClickListener(this);
        scegliSfondo.setOnClickListener(this);*/





        if(immagine != null){

            Picasso.get().load(mImageUri).into(scegliSfondo);
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventoActivity3.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler uscire dalla creazione dell'evento? I dati inseriti andranno persi");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        startActivity(new Intent(AddEventoActivity3.this,AmmHomeFragment.class));
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
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(AddEventoActivity3.this, AddEventoActivity2.class);
                intent.putExtra("titolo", titolo);
                intent.putExtra("descrizione", descrizione);
                intent.putExtra("data", data);
                intent.putExtra("latitudine", latitudine);
                intent.putExtra("longitudine", longitude);
                intent.putExtra("luogo", luogo);
                intent.putExtra("immagine", mImageUri);
                startActivity(intent);

            }
        });

        scegliSfondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(230, 180)
                        .start(AddEventoActivity3.this);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddEventoActivity3.this, R.style.AlertDialogStyle);
                    // Setting Dialog Title
                    //builder.setTitle("Internet non disponibile");

                    // Setting Dialog Message
                    builder.setMessage("Sei sicuro di voler pubblicare l'evento?");

                    // On pressing the Settings button.
                    builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            admin();
                            Intent intent = new Intent(getApplicationContext(), AmmHomeFragment.class);
                            startActivity(intent);
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

                }else{
                    Toast.makeText(AddEventoActivity3.this,"Non hai inserito nessuna foto",Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private void admin(){

        final String titolo = getIntent().getStringExtra("titolo");
        final String descrizione = getIntent().getStringExtra("descrizione");
        final String data = getIntent().getStringExtra("data");
        final Double latitudine =getIntent().getDoubleExtra("latitudine",0.0);
        final Double longitude = getIntent().getDoubleExtra("longitudine",0.0);
        final String luogo = getIntent().getStringExtra("luogo");

        //viene assegnato il testo inserito dall'admin
        //final String date = editDate.getText().toString().trim();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference quizRef = rootRef.child("Eventi");
        final String id = quizRef.push().getKey();
        firebaseAuth = FirebaseAuth.getInstance();

        //CARICAMENTO EVENTO
        if (mImageUri != null) {
            final StorageReference ref = FirebaseStorage.getInstance().getReference("immaginiEventi/" + System.currentTimeMillis() + ".jpg");

            mUploadTask = ref.putFile(mImageUri); //VIENE INSERITO IL FILE NELLO STORAGE
            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        final String nome1= user.getDisplayName().replaceAll("%20" ," ");

                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                        DatabaseEvento upload = new DatabaseEvento(id,data, titolo, latitudine, longitude, luogo.toLowerCase(), downloadUri.toString(), descrizione,0., "1",user.getUid());
                        databaseReference.child("Eventi").child(id).setValue(upload);

                        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
                        databaseReferenceutente.child("UserID").child("Utenti").child(nome1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                databaseReference.child("UserID").child("Utenti").child(nome1).child("Eventi Creati").child(id).setValue(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
        Toast.makeText(AddEventoActivity3.this, "Evento creato con successo", Toast.LENGTH_LONG).show();


    }


    private String getFileExtension(Uri uri){
        ContentResolver cR = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Picasso.get().load(mImageUri).into(scegliSfondo);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
