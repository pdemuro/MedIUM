package com.medium.progettomedium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn, buttonAdmin;
    private TextInputLayout editTextEmail;
    private TextInputLayout editTextPassword;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
         /* if(user.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }else{
                Toast.makeText(this,"L'account non è verificato: controlla la mail",Toast.LENGTH_SHORT).show();
            }*/

           startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        editTextEmail =  findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        //buttonAdmin = (Button) findViewById(R.id.buttonAdmin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
       // buttonAdmin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    private void userLogin(){
        final String email = editTextEmail.getEditText().getText().toString().trim();
        final String password = editTextPassword.getEditText().getText().toString().trim();
        boolean var = true;


        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Non hai inserito l'email");
            var = false;

        } else {
            editTextEmail.setError(null);


        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Non hai inserito la pass");
            var = false;
        } else {
            editTextPassword.setError(null);

        }

        if(var) {


            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        progressDialog.setMessage("Accesso in corso...");
                        progressDialog.show();
                        //Inizio attivita del profilo
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        editTextPassword.setError("Password non valida");
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        editTextEmail.setError("Email non valida");
                    } else {

                        progressDialog.setMessage("Errore sconosciuto");
                    }
                }
            });
        }
    }
    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }
        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
       /* if(view == buttonAdmin){
            finish();
            startActivity(new Intent(this, ProfileAdmin.class));
        }*/
    }
}
