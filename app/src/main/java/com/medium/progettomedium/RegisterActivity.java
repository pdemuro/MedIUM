package com.medium.progettomedium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.medium.progettomedium.Model.DatabaseUtente;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button buttonRegister;
    private Button buttonCaricaCarta;
    private EditText editTextEmail;
    private EditText editTextPec;
    private EditText editTextData;
    private EditText editTextLuogo;
    private EditText editTextResidenza;
    private EditText editTextIndirizzo;
    private EditText editTextCap;
    private EditText editTextPassword;
    private EditText editTextConfPassword;
    private EditText editTextCodiceAutorizzazione;
    private TextView textViewSignIn;
    private EditText editTextName,editTextCognome, editTextPhone, editTextCategory;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String tipoUtente="";
    private FirebaseStorage mStorage;
    private static final int CHOOSE_IMAGE = 101;
    private Uri mImageUri;
    private ImageView imageView;
    public String image_url;
    private UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //Attivita del profilo
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextCognome = (EditText) findViewById(R.id.editTextCognome);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPec = (EditText) findViewById(R.id.pec);
        editTextData = (EditText) findViewById(R.id.editTextData);
        editTextIndirizzo = (EditText) findViewById(R.id.editTextIndirizzo);
        editTextCap = (EditText) findViewById(R.id.editTextCap);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextCategory = (EditText) findViewById(R.id.editTextCategory);
        editTextLuogo = (EditText) findViewById(R.id.editTextLuogo);
        editTextResidenza = (EditText) findViewById(R.id.editTextResidenza);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfPassword = (EditText) findViewById(R.id.editTextConfPassword);
        editTextCodiceAutorizzazione = (EditText) findViewById(R.id.editTextCodiceAutorizzazione);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonCaricaCarta = (Button) findViewById(R.id.button_carica_carta);
        imageView = findViewById(R.id.immagine_carta_identita);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_nuovoUtente, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        progressDialog = new ProgressDialog(this);
        buttonCaricaCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //openFileChooser();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(RegisterActivity.this);

            }
        });
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void registerUser(){
        String email;
        if(tipoUtente.equals( "Utente")){
            email = editTextEmail.getText().toString().trim();
        }
        else{
            email = editTextPec.getText().toString().trim();
        }
        String password = editTextPassword.getText().toString().trim();
        String confPassword = editTextConfPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(email)){
            Toast.makeText(this,"Inserisci l'email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Inserisci la password",Toast.LENGTH_SHORT).show();
            return;

        }else if(password.length() < 8){
            Toast.makeText(this,"La password deve avere almeno 8 caratteri",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(confPassword)){
            Toast.makeText(this,"conferma la password",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!password.equals(confPassword)){
            Toast.makeText(this,"la conferma è errata",Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Registrazione in corso...");
        progressDialog.show();

        //creazione nuovo utente

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Attivita del profilo
                    saveUserInformation();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RegisterActivity.this, "Email di Verifica Inviata", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Errore nella registrazione,Riprova", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });


    }

    private void saveUserInformation() {
        if (tipoUtente.equals( "Utente")) {
            String name = editTextName.getText().toString().trim();
            String cognome = editTextCognome.getText().toString().trim();
            String fullname = name + " " + cognome;
            String email = editTextEmail.getText().toString().trim();
            String userID = firebaseAuth.getUid();
            String category = "Utente";
            DatabaseUtente databaseUtente = new DatabaseUtente(fullname, email,userID, category);


            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name + " " + cognome).build();
                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
            }
            databaseReference.child("UserID").child("Utenti").child(name + " " + cognome).setValue(databaseUtente);
            Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
        }

        if (tipoUtente.equals( "Organizzatore")) {
            String name = editTextName.getText().toString().trim();
            String cognome = editTextCognome.getText().toString().trim();
            String fullname = name + " " + cognome;
            String category = editTextCategory.getText().toString().trim();
            String pec = editTextPec.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String data = editTextData.getText().toString().trim();
            String luogo = editTextLuogo.getText().toString().trim();
            String indirizzo =  editTextIndirizzo.getText().toString().trim();
            String cap =  editTextCap.getText().toString().trim();
            String residenza = editTextResidenza.getText().toString().trim();
            DatabaseUtente databaseUtente = new DatabaseUtente(fullname, category, pec, phone, data, luogo, residenza,indirizzo, cap );

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name + " " + cognome).build();
                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
            }

            databaseReference.child("UserID").child("Organizzatori").child(name + " " + cognome).setValue(databaseUtente);
            Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();


        }

        if(view == textViewSignIn){
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

        switch (text) {
            case "Utente":
                editTextEmail.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextConfPassword.setVisibility(View.VISIBLE);
                editTextPec.setVisibility(View.GONE);
                editTextPhone.setVisibility(View.GONE);
                editTextCategory.setVisibility(View.GONE);
                editTextData.setVisibility(View.GONE);
                editTextLuogo.setVisibility(View.GONE);
                editTextResidenza.setVisibility(View.GONE);
                editTextIndirizzo.setVisibility(View.GONE);
                editTextCap.setVisibility(View.GONE);
                editTextCodiceAutorizzazione.setVisibility(View.GONE);
                buttonCaricaCarta.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                tipoUtente = "Utente";
                break;
            case "Organizzatore":
                editTextEmail.setVisibility(View.GONE);
                editTextPec.setVisibility(View.VISIBLE);
                editTextCategory.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextConfPassword.setVisibility(View.VISIBLE);
                editTextPhone.setVisibility(View.VISIBLE);
                editTextData.setVisibility(View.VISIBLE);
                editTextLuogo.setVisibility(View.VISIBLE);
                editTextResidenza.setVisibility(View.VISIBLE);
                editTextIndirizzo.setVisibility(View.VISIBLE);
                editTextCap.setVisibility(View.VISIBLE);
                editTextCodiceAutorizzazione.setVisibility(View.GONE);
                buttonCaricaCarta.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                tipoUtente = "Organizzatore";
                break;
        }

    }

    private void openFileChooser(){
        //INTENT PER SCEGLIERE IMMAGINE
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona carta identità"), CHOOSE_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            }
        }


        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            imageView.setImageBitmap(bitmap);
            uploadImageToFirebaseStorage();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void uploadImageToFirebaseStorage() {
        //StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo/"+System.currentTimeMillis()+ ".jpg");

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("carteidentita/" + System.currentTimeMillis() + ".jpg");

        //CARICAMENTO EVENTO
        if (mImageUri != null) {

            mStorage = FirebaseStorage.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser();

            mUploadTask = profileImageRef.putFile(mImageUri); //VIENE INSERITO IL FILE NELLO STORAGE
            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult(); //LINK SFONDO
                        image_url = downloadUri.toString();
                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

