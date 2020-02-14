package com.medium.progettomedium.Fragment;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medium.progettomedium.MainActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddEventFragment extends Fragment implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editDescrizione;
    public String luogo;
    public HashMap<String, String> prenotazioni = new HashMap<>();
    private Button buttonEvent, getPlace, scegliSfondo;
    public Double latitude;
    public Double longitude;
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private UploadTask mUploadTask;
    private TextView boxData;
    private String data2;
    private static final String TAG = "ActivityAdmin";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceutente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_add_event, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference(); //carica database
        boxData = (TextView) view.findViewById(R.id.editDate);
        editTitolo = (EditText) view.findViewById(R.id.editTitolo);
        editDescrizione = (EditText) view.findViewById(R.id.editDescrizione);
        buttonEvent = (Button) view.findViewById(R.id.buttonEvent);
        getPlace = (Button) view.findViewById(R.id.getPlace);
        scegliSfondo = (Button) view.findViewById(R.id.sfondo);
        boxData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
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

                boxData.setText(date);


            }
        };
        mStorageRef = FirebaseStorage.getInstance().getReference(); //caricamento file dal database

        buttonEvent.setOnClickListener(this);
        getPlace.setOnClickListener(this);
        scegliSfondo.setOnClickListener(this);
        return view;
    }

    private void admin(){

        //viene assegnato il testo inserito dall'admin
        //final String date = editDate.getText().toString().trim();
        final String titolo = editTitolo.getText().toString().trim();
        final String descrizione = editDescrizione.getText().toString().trim();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference quizRef = rootRef.child("Eventi");
        final String id = quizRef.push().getKey();
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
                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                        DatabaseEvento upload = new DatabaseEvento(id,data2, titolo, latitude, longitude, luogo.toLowerCase(), prenotazioni, downloadUri.toString(), descrizione,0., "1");
                        databaseReference.child("Eventi").child(titolo).setValue(upload);

                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
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

        Toast.makeText(getContext(), "Informazioni Salvate", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {

        //CARICA EVENTO
        if(view == buttonEvent){
            admin();
            startActivity(new Intent(getContext(), MainActivity.class));
        }

        //SCEGLI POSIZIONE
        if(view == getPlace){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try{
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            }
            catch(GooglePlayServicesRepairableException e){
                e.printStackTrace();
            }
            catch (GooglePlayServicesNotAvailableException e ){
                e.printStackTrace();
            }
        }

        //SCEGLI SFONDO
        if(view == scegliSfondo){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(432,180)
                    .start(getContext(),this);
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(getContext(), data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo = (String) place.getName();
        }
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}

