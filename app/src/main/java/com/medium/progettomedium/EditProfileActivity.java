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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.medium.progettomedium.Fragment.AmmProfileFragment;
import com.medium.progettomedium.Fragment.ProfileFragment;
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

    private FirebaseStorage mStorage;

    private String image_url;
    private UploadTask mUploadTask;
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
                    residenza.setText(user.getResidenza());
                    cap.setText(user.getCap());
                    telefono.setVisibility(View.VISIBLE);
                    data.setVisibility(View.VISIBLE);
                    luogo.setVisibility(View.VISIBLE);
                    indirizzo.setVisibility(View.VISIBLE);
                    residenza.setVisibility(View.VISIBLE);
                    cap.setVisibility(View.VISIBLE);

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

                FirebaseUser nome = firebaseAuth.getCurrentUser();
                String nome1= nome.getDisplayName().replaceAll("%20" ," ");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserID").child("Utenti").child(nome1);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                        FirebaseUser photoUser= firebaseAuth.getCurrentUser();
                        if( user.getCategory().equals("Utente" ) ) {


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
                                    Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
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

                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AlertDialogStyle);
                            // Setting Dialog Title
                            //builder.setTitle("Internet non disponibile");

                            // Setting Dialog Message
                            builder.setMessage("Sei sicuro di voler salvare le informazioni del profilo?");

                            // On pressing the Settings button.
                            builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    updateProfileAmm(nomeU.getText().toString(),cognome.getText().toString(),
                                            email.getText().toString(),bio.getText().toString(),telefono.getText().toString(),data.getText().toString()
                                    ,luogo.getText().toString(),residenza.getText().toString(),indirizzo.getText().toString(),cap.getText().toString());
                                    Intent intent = new Intent(EditProfileActivity.this, AmmProfileFragment.class);
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

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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

    private void updateProfileAmm(String nomeU, String cognome, String email,String bio, String telefono, String data, String luogo, String residenza, String indirizzo, String cap){
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);

        HashMap<String, Object> map = new HashMap<>();
        map.put("nome", nomeU);
        map.put("cognome", cognome);
        map.put("mail", email);
        map.put("descrizione", bio);
        map.put("phone", telefono);
        map.put("data", data);
        map.put("luogo", luogo);
        map.put("residenza", residenza);
        map.put("indirizzo", indirizzo);
        map.put("cap", cap);

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
                        Intent intent = new Intent(EditProfileActivity.this, EditProfileActivity.class);
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
    private void saveInformation() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && image_url != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(image_url)).build();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("UserID").child("Utenti");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DatabaseUtente user = snapshot.getValue(DatabaseUtente.class);
                        String nome = user.getNome() + " " + user.getCognome();

                        FirebaseUser user2 = firebaseAuth.getCurrentUser();
                        if (nome.equals(user2.getDisplayName())) {
                            //user.setImageUrl(image_url);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome).child("imageUrl");


                            reference.setValue(image_url);

                            //getContext().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            // new ProfileFragment()).commit();

                            pd.dismiss();
                            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                            intent.putExtra("profileid",user.getId());
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                uploadImageToFirebaseStorage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo/" + System.currentTimeMillis() + ".jpg");

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
                        saveInformation();
                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }
}
