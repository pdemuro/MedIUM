package com.medium.progettomedium.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.Adapter.AdaptEventoModificabile;
import com.medium.progettomedium.Adapter.AdaptEventoUtente;
import com.medium.progettomedium.Adapter.MyFotosAdapter;
import com.medium.progettomedium.AddEventoActivity;
import com.medium.progettomedium.AddPostActivity;
import com.medium.progettomedium.EditProfileActivity;
import com.medium.progettomedium.LoginActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.Model.Post;
import com.medium.progettomedium.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

//import com.eventisardegna.shardana.eventisardegna.EditProfileActivity;
//import com.eventisardegna.shardana.eventisardegna.FollowersActivity;
//import com.eventisardegna.shardana.eventisardegna.OptionsActivity;

public class AmmProfileFragment extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    ImageView image_profile, options;
    TextView posts, followers, following, fullname, category, username,email,telefono,data,luogo,indirizzo,cap,residenza;
    Button edit_profile;

    private List<String> mySaves;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private RecyclerView recyclerView_saves;
    private List<DatabaseEvento> postList_saves;
    private Uri mImageUri;
    private String image_url;
    private FirebaseStorage mStorage;
    boolean doubleTap = false;
    private UploadTask mUploadTask;
    FirebaseAuth firebaseAuth;
    private AdaptEventoModificabile.OnItemClickListener itemClickListener2;
    private AdaptEventoUtente.OnItemClickListener itemClickListenerutente;
    private List<Post> postList;
    ImageButton my_fotos, saved_fotos;
    CardView iMieiPost, iMieiEventi;
    TextView logout;
    private DatabaseReference databaseReferenceutente;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;

    private AdaptEventoModificabile adaptEventoModificabile;
    private AdaptEventoUtente adapterutente;
    private RecyclerView listaEventiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilo_amm);

        bottomNavigationView = findViewById(R.id.bottom_navigation_amm);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();
        SharedPreferences prefs =getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("UserID", "none");

        image_profile = findViewById(R.id.image_profile5);
        //posts = view.findViewById(R.id.posts);
        //followers = view.findViewById(R.id.followers);
        //following = view.findViewById(R.id.following);
        fullname = findViewById(R.id.fullname3);
        category = findViewById(R.id.category3);
        edit_profile = findViewById(R.id.edit_profile4);
        telefono = findViewById(R.id.telefono);
        data = findViewById(R.id.data);
        luogo = findViewById(R.id.luogo);
        indirizzo = findViewById(R.id.indirizzo);
        residenza = findViewById(R.id.residenza);
        cap = findViewById(R.id.cap);
        email = findViewById(R.id.email);
        //username = view.findViewById(R.id.username);
     //   my_fotos = view.findViewById(R.id.my_fotos);

      //  saved_fotos = view.findViewById(R.id.saved_fotos);
        logout= findViewById(R.id.logout);

        userInfo();

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Modifica Profilo")){

                    startActivity(new Intent(getApplication(), EditProfileActivity.class));

                }
            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AmmProfileFragment.this, R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler uscire Dal profilo?");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AmmProfileFragment.this, LoginActivity.class));
                        // Toast.makeText(getContext(),"Modifica annullata",Toast.LENGTH_SHORT).show();
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



    private void userInfo(){
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getApplicationContext() == null){
                    return;
                }
                DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                if(user.category.equals("Utente")){
                    //my_fotos.setVisibility(View.GONE);
                    //saved_fotos.setVisibility(View.GONE);
                }
                FirebaseUser photoUser= firebaseAuth.getCurrentUser();

                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(image_profile);

                image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(60, 60)
                                .start(AmmProfileFragment.this);


                    }
                });

                String nome = user.getNome()+" "+user.getCognome();
                fullname.setText(nome);
                category.setText(user.getCategory());
                telefono.setText(user.getPhone());
                data.setText(user.getData());
                luogo.setText(user.getLuogo());
                indirizzo.setText(user.getIndirizzo());
                residenza.setText(user.getResidenza());
                cap.setText(user.getCap());
                email.setText(user.getMail());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void saveInformation() {
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
                            Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
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
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_profileAmm:
                    SharedPreferences.Editor editor2 = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor2.putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor2.apply();
                    startActivity( new Intent(getApplicationContext(), AmmProfileFragment.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.nav_richieste:
                    overridePendingTransition(R.anim.animation_enter,
                            R.anim.animation_leave);
                    startActivity( new Intent(getApplicationContext(), AmmHomeFragment.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.nav_add:
                    startActivity( new Intent(getApplicationContext(), AddEventoActivity.class));
                    break;


            }
            return true;
        }
    };

    public void onBackPressed() {

        startActivity(new Intent(AmmProfileFragment.this,AmmHomeFragment.class));

    }


}
