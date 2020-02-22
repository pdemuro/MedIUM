package com.medium.progettomedium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.medium.progettomedium.Fragment.AmmHomeFragment;
import com.medium.progettomedium.Fragment.ProfileFragment;
import com.medium.progettomedium.Fragment.UtenteHomeFragment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    ImageView  image_added;
            ImageView close,image_profile;
    TextView post,pubblica,username;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        pubblica = findViewById(R.id.pubblica);
        image_added = findViewById(R.id.addPost);
        close=findViewById(R.id.close);
        description = findViewById(R.id.descrizione);

        storageRef = FirebaseStorage.getInstance().getReference("posts");



        pubblica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Pubblicare il post?");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        Toast.makeText(AddPostActivity.this,"Post Creato",Toast.LENGTH_SHORT).show();
                        uploadImage_10();
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                       /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("publisherid", firebaseUser.getUid());
                        startActivity(intent);*/
                        startActivity(new Intent(AddPostActivity.this, AmmHomeFragment.class));
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
        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(AddPostActivity.this);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Annullare la creazione del post? I dati non verranno salvati");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        Toast.makeText(AddPostActivity.this,"Creazione evento annullata",Toast.LENGTH_SHORT).show();
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


    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");

        if (mImageUri != null){
            final StorageReference fileReference = FirebaseStorage.getInstance().getReference("immaginiPost/" + System.currentTimeMillis() + ".jpg");


            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", nome1);

                        reference.child(postid).setValue(hashMap);


                        startActivity(new Intent(AddPostActivity.this, ProfileFragment.class));
                        finish();

                    } else {
                        Toast.makeText(AddPostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(AddPostActivity.this, "Nessuna immagine selazionata", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            image_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Qualcosa è andato storto!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddPostActivity.this, AddPostActivity.class));
            finish();
        }
    }
    public void onBackPressed() {

        startActivity(new Intent(AddPostActivity.this, UtenteHomeFragment.class));

    }
}

