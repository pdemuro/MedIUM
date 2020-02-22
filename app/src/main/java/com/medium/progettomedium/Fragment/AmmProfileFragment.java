package com.medium.progettomedium.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.medium.progettomedium.EditProfileActivity;
import com.medium.progettomedium.LoginActivity;
import com.medium.progettomedium.MainActivity;
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

public class AmmProfileFragment extends Fragment {

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.profilo_amm, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("UserID", "none");

        image_profile = view.findViewById(R.id.image_profile5);
        //posts = view.findViewById(R.id.posts);
        //followers = view.findViewById(R.id.followers);
        //following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname3);
        category = view.findViewById(R.id.category3);
        edit_profile = view.findViewById(R.id.edit_profile4);
        telefono = view.findViewById(R.id.telefono);
        data = view.findViewById(R.id.data);
        luogo = view.findViewById(R.id.luogo);
        indirizzo = view.findViewById(R.id.indirizzo);
        residenza = view.findViewById(R.id.residenza);
        cap = view.findViewById(R.id.cap);
        email = view.findViewById(R.id.email);
        //username = view.findViewById(R.id.username);
     //   my_fotos = view.findViewById(R.id.my_fotos);

      //  saved_fotos = view.findViewById(R.id.saved_fotos);
        logout=view.findViewById(R.id.logout);

        userInfo();

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Modifica Profilo")){

                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                }
            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getContext(), LoginActivity.class));


            }
        });

        return view;
    }



    private void userInfo(){
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                DatabaseUtente user = dataSnapshot.getValue(DatabaseUtente.class);
                if(user.category.equals("Utente")){
                    //my_fotos.setVisibility(View.GONE);
                    //saved_fotos.setVisibility(View.GONE);
                }
                FirebaseUser photoUser= firebaseAuth.getCurrentUser();

                Glide.with(getContext()).load(user.getImageUrl()).into(image_profile);

                image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(60, 60)
                                .start(getContext(), AmmProfileFragment.this);


                    }
                });

                String nome = user.getNome()+" "+user.getCognome();
                fullname.setText(nome);
                category.setText(user.getCategory());
                telefono.setText(user.getPhone());
                data.setText(user.getData());
                luogo.setText(user.getLuogo());
                indirizzo.setText(user.getIndirizzo());
                residenza.setText(user.getIndirizzo());
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
                            Intent intent = new Intent(getContext(), MainActivity.class);
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



}
