package com.medium.progettomedium;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.medium.progettomedium.Model.DatabaseUtente;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText nomeU,cognome, email, bio,telefono,data,luogo,indirizzo,residenza,cap;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private Uri mImageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        nomeU = findViewById(R.id.nomeU);
        cognome = findViewById(R.id.cognomeU);
        email = findViewById(R.id.email);
        bio = findViewById(R.id.bio);
        telefono = findViewById(R.id.telefono);
        data = findViewById(R.id.data);
        luogo = findViewById(R.id.luogo);
        indirizzo = findViewById(R.id.indirizzo);
        residenza = findViewById(R.id.residenza);
        cap = findViewById(R.id.cap);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo");

        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").child(nome1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                FirebaseUser photoUser= firebaseAuth.getCurrentUser();

                nomeU.setText(user.getNome());
                cognome.setText(user.getCognome());
                email.setText(user.getMail());
                bio.setText(user.getDescrizione());
                if (user.getImageUrl() != null) {
                    Glide.with(EditProfileActivity.this).load(user.getImageUrl()).into(image_profile);

                }
                image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(60, 60)
                                .start(EditProfileActivity.this);
                    }
                });

                if(user.getCategory().equals("Organizzatore")){
                    telefono.setText(user.getPhone());
                    data.setText(user.getData());
                    luogo.setText(user.getLuogo());
                    indirizzo.setText(user.getIndirizzo());
                    residenza.setText(user.getIndirizzo());
                    cap.setText(user.getCap());
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo");

                FirebaseUser nome = firebaseAuth.getCurrentUser();
                String nome1= nome.getDisplayName().replaceAll("%20" ," ");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").child(nome1);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                        FirebaseUser photoUser= firebaseAuth.getCurrentUser();
                        if( user.getCategory().equals("Utente" ) &&! user.getNome().equals(nomeU.getText().toString()) || ! user.getCognome().equals(cognome.getText().toString()) ||
                                        ! user.getMail().equals(email.getText().toString()) ||! user.getDescrizione().equals(bio.getText().toString()) ) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AlertDialogStyle);
                            // Setting Dialog Title
                            //builder.setTitle("Internet non disponibile");

                            // Setting Dialog Message
                            builder.setMessage("Sei sicuro di uscire dalla modificare del profilo?");

                            // On pressing the Settings button.
                            builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
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

                        }else if(user.getCategory().equals("Organizzatore" ) &&! user.getNome().equals(nomeU.getText().toString()) || ! user.getCognome().equals(cognome.getText().toString()) ||
                                ! user.getMail().equals(email.getText().toString()) ||! user.getDescrizione().equals(bio.getText().toString()) ||
                                ! user.getPhone().equals(telefono.getText().toString())||! user.getLuogo().equals(luogo.getText().toString()) ||
                                ! user.getIndirizzo().equals(indirizzo.getText().toString()) ||! user.getCap().equals(cap.getText().toString())){

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AlertDialogStyle);
                            // Setting Dialog Title
                            //builder.setTitle("Internet non disponibile");

                            // Setting Dialog Message
                            builder.setMessage("Sei sicuro di uscire dalla modificare del profilo?");

                            // On pressing the Settings button.
                            builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
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
                            finish();
                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                }


        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler salvare le informazioni del profilo?");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        updateProfile(nomeU.getText().toString(),cognome.getText().toString(),
                                email.getText().toString(),bio.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("publisherid", firebaseUser.getUid());
                        startActivity(intent);
                        Toast.makeText(EditProfileActivity.this,"Informazioni aggiornate",Toast.LENGTH_SHORT).show();



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

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });
    }

    private void updateProfile(String nomeU, String cognome, String email,String bio){
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);

        HashMap<String, Object> map = new HashMap<>();
        map.put("nome", nomeU);
        map.put("cognome", cognome);
        map.put("mail", email);
        map.put("descrizione", bio);

        reference.updateChildren(map);

        Toast.makeText(EditProfileActivity.this, "Informazioni Aggiornate!", Toast.LENGTH_SHORT).show();

        //startActivity(new Intent(this, MainActivity.class));
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage(){
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        final String nome1= nome.getDisplayName().replaceAll("%20" ," ");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("imageUrl", ""+miUrlOk);
                        reference.updateChildren(map1);

                        pd.dismiss();
                        String id = reference.child("id").toString();
                        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                        intent.putExtra("profileid",id);
                        startActivity(intent);

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(EditProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();


        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
