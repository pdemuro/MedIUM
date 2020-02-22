package com.medium.progettomedium;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button buttonRegister;
    private TextInputLayout editTextEmail;
    private Button editTextData;
    private TextInputLayout editTextLuogo;
    private TextInputLayout editTextResidenza;
    private TextInputLayout editTextIndirizzo;
    private TextInputLayout editTextCap;
    private TextInputLayout editTextPassword;
    private TextInputLayout editTextConfPassword;
    private TextView textViewSignIn;
    private TextInputLayout editTextName,editTextCognome, editTextPhone;
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
    private String data2;
    private String date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        //DA CONTROLLARE
        /*if(firebaseAuth.getCurrentUser() != null){
            //Attivita del profilo
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }*/

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = findViewById(R.id.editTextName);
        editTextCognome = findViewById(R.id.editTextCognome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextData = (Button) findViewById(R.id.editTextData);
        editTextIndirizzo = findViewById(R.id.editTextIndirizzo);
        editTextCap =  findViewById(R.id.editTextCap);
        editTextPhone =  findViewById(R.id.editTextPhone);
        editTextLuogo =  findViewById(R.id.editTextLuogo);
        editTextResidenza =  findViewById(R.id.editTextResidenza);
        editTextPassword =  findViewById(R.id.editTextPassword);
        editTextConfPassword = findViewById(R.id.editTextConfPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_nuovoUtente, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        progressDialog = new ProgressDialog(this);
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
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

                editTextData.setText(data2);


            }
        };
    }

    private void registerUser() {

        String nome = editTextName.getEditText().getText().toString().trim();
        String cognome = editTextCognome.getEditText().getText().toString().trim();
        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();
        String confPassword = editTextConfPassword.getEditText().getText().toString().trim();
        String telefono = editTextPhone.getEditText().getText().toString().trim();
        String data = editTextData.getText().toString().trim();
        String luogo = editTextLuogo.getEditText().getText().toString().trim();
        String indirizzo = editTextIndirizzo.getEditText().getText().toString().trim();
        String residenza = editTextResidenza.getEditText().getText().toString().trim();
        String cap = editTextCap.getEditText().getText().toString().trim();

        boolean var = true;


        if (TextUtils.isEmpty(nome)) {
            editTextName.setError("Non hai inserito il nome");
            var = false;

        } else {
            editTextName.setError(null);


        }
        if (TextUtils.isEmpty(cognome)) {
            editTextCognome.setError("Non hai inserito il cognome");
            var = false;
        } else {
            editTextCognome.setError(null);

        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Non hai inserito la email");
            var = false;
        } else {
            editTextEmail.setError(null);

        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Non hai inserito la password");
            var = false;

        } else if (password.length() < 8) {
            editTextPassword.setError("La password deve avere almeno 8 caratteri");
            var = false;

        }

        if (TextUtils.isEmpty(confPassword)) {
            editTextConfPassword.setError("Non hai confermato la password");
            var = false;

        } else {
            editTextConfPassword.setError(null);

        }
        if (!password.equals(confPassword)) {
            editTextConfPassword.setError("Le password non coincidono");
            var = false;

        } else {
            editTextConfPassword.setError(null);

        }
        if (tipoUtente.equals("Organizzatore")) {
            if (TextUtils.isEmpty(telefono)) {
                editTextPhone.setError("Non hai inserito il telefono");
                var = false;
            } else {
                editTextPhone.setError(null);
            }

            if (TextUtils.isEmpty(data)) {
                editTextData.setError("Non hai inserito la data di nascita");
                var = false;
            } else {
                editTextData.setError(null);

            }

            if (TextUtils.isEmpty(luogo)) {
                editTextLuogo.setError("Non hai inserito il luogo di nascita");
                var = false;
            } else {
                editTextLuogo.setError(null);

            }

            if (TextUtils.isEmpty(indirizzo)) {
                editTextIndirizzo.setError("Non hai inserito l'indirizzo");
                var = false;
            } else {
                editTextIndirizzo.setError(null);

            }
            if (TextUtils.isEmpty(residenza)) {
                editTextResidenza.setError("Non hai inserito la residenza");
                var = false;
            } else {
                editTextResidenza.setError(null);

            }
            if (TextUtils.isEmpty(cap)) {
                editTextCap.setError("Non hai inserito il CAP");
                var = false;
            } else {
                editTextCap.setError(null);

            }
        }

        if(var) {


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
                       /* if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegisterActivity.this, "Email di Verifica Inviata", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }*/
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                    } else {
                        Toast.makeText(RegisterActivity.this, "Errore nella registrazione,Riprova", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }

    }

    private void saveUserInformation() {
        if (tipoUtente.equals( "Utente")) {

            String name = editTextName.getEditText().getText().toString().trim();
            String cognome = editTextCognome.getEditText().getText().toString().trim();
            String fullname = name + " " + cognome;
            String email = editTextEmail.getEditText().getText().toString().trim();
            String userID = firebaseAuth.getUid();
            String category = "Utente";
            DatabaseUtente databaseUtente = new DatabaseUtente(name,cognome, email,userID, category,"https://firebasestorage.googleapis.com/v0/b/progettomedium-76d21.appspot.com/o/immaginiprofilo%2Fprofilo-png-2.png?alt=media&token=54d738cc-06f1-41c6-9f33-541b391b12ba","");


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
            String name = editTextName.getEditText().getText().toString().trim();
            String cognome = editTextCognome.getEditText().getText().toString().trim();
            String fullname = name + " " + cognome;
            String category = "Organizzatore";
            String mail = editTextEmail.getEditText().getText().toString().trim();
            String phone = editTextPhone.getEditText().getText().toString().trim();
            String data = editTextData.getText().toString().trim();
            String luogo = editTextLuogo.getEditText().getText().toString().trim();
            String indirizzo =  editTextIndirizzo.getEditText().getText().toString().trim();
            String cap =  editTextCap.getEditText().getText().toString().trim();
            String residenza = editTextResidenza.getEditText().getText().toString().trim();
            String userID = firebaseAuth.getUid();
            DatabaseUtente databaseUtente = new DatabaseUtente(name, cognome, mail, userID,category, phone, data, luogo, residenza,indirizzo, cap,"https://firebasestorage.googleapis.com/v0/b/progettomedium-76d21.appspot.com/o/immaginiprofilo%2Fprofilo-png-2.png?alt=media&token=54d738cc-06f1-41c6-9f33-541b391b12ba");

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
                editTextPhone.setVisibility(View.GONE);
                editTextData.setVisibility(View.GONE);
                editTextLuogo.setVisibility(View.GONE);
                editTextResidenza.setVisibility(View.GONE);
                editTextIndirizzo.setVisibility(View.GONE);
                editTextCap.setVisibility(View.GONE);
                tipoUtente = "Utente";
                break;
            case "Organizzatore":
                editTextEmail.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextConfPassword.setVisibility(View.VISIBLE);
                editTextPhone.setVisibility(View.VISIBLE);
                editTextData.setVisibility(View.VISIBLE);
                editTextLuogo.setVisibility(View.VISIBLE);
                editTextResidenza.setVisibility(View.VISIBLE);
                editTextIndirizzo.setVisibility(View.VISIBLE);
                editTextCap.setVisibility(View.VISIBLE);
                tipoUtente = "Organizzatore";
                break;
        }

    }




    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

