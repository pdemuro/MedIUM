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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.Adapter.AdaptEvento;
import com.medium.progettomedium.EditProfileActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

//import com.eventisardegna.shardana.eventisardegna.EditProfileActivity;
//import com.eventisardegna.shardana.eventisardegna.FollowersActivity;
//import com.eventisardegna.shardana.eventisardegna.OptionsActivity;

public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    TextView posts, followers, following, fullname, category, username;
    Button edit_profile;

    private List<String> mySaves;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private AdaptEvento myFotosAdapter;
    private List<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private RecyclerView recyclerView_saves;
    private AdaptEvento myFotosAdapter_saves;
    private List<DatabaseEvento> postList_saves;
    private Uri mImageUri;
    private String image_url;
    private FirebaseStorage mStorage;
    private UploadTask mUploadTask;
    FirebaseAuth firebaseAuth;
    private AdaptEvento.OnItemClickListener itemClickListener;

    ImageButton my_fotos, saved_fotos;


    private DatabaseReference databaseReference;
    private AdaptEvento adapter;
    private RecyclerView listaEventiView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("UserID", "none");

        image_profile = view.findViewById(R.id.image_profile);
        //posts = view.findViewById(R.id.posts);
        //followers = view.findViewById(R.id.followers);
        //following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        category = view.findViewById(R.id.category);
        edit_profile = view.findViewById(R.id.edit_profile);
        //username = view.findViewById(R.id.username);
       // my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        options = view.findViewById(R.id.options);
/*
        recyclerView = view.findViewById(R.id.row_adda);
       // recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //recyclerView.setHasFixedSize(true);
        //LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
//        recyclerView.setLayoutManager(mLayoutManager);
      // recyclerView.setAdapter(myFotosAdapter);
*/
       // recyclerView_saves = view.findViewById(R.id.recycler_view_save);
     /*   recyclerView_saves.setHasFixedSize(true);
        //LinearLayoutManager mLayoutManagers = new GridLayoutManager(getContext(), 3);
        //recyclerView_saves.setLayoutManager(mLayoutManagers);
        postList_saves = new ArrayList<>();
        myFotosAdapter_saves = new AdaptEvento(getContext(), postList_saves, itemClickListener);
*/
        listaEventiView = (RecyclerView) view.findViewById(R.id.recycler_view_save);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();
        postList_saves = new ArrayList<>();
        // recyclerView.setVisibility(View.VISIBLE);
       //recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        //getFollowers();
        //getNrPosts();
       // myFotos();
      mySaves();

       edit_profile.setText("Edit Profile");


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){

                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                }
            }
        });



       /* my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });
*/

/*        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });*/

        return view;
    }

    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.
        int flag = 0;
        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        for(DatabaseEvento eventi: eventi){
            if(eventi.getTitolo().equals(doc.getTitolo())){
                flag = 1;
            }
        }
        if(flag == 0) {
            DatabaseEvento.date_collection_arr.add(eve);
            eventi.add(doc);
        }
        adapter = new AdaptEvento(getContext(), eventi, itemClickListener);

        //listaEventiView.setAdapter(adapter);
        listaEventiView.setAdapter(new AdaptEvento(getContext(), eventi, new AdaptEvento.OnItemClickListener() {
            @Override public void onItemClick(DatabaseEvento item) {

                String mTitolo = item.getTitolo();
                String mLuogo = item.getLuogo();
                String mData = item.getDate();
                String mImage = item.getImmagine();
                Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliEvento.class);
                intent.putExtra("titolo", mTitolo);
                intent.putExtra("luogo", mLuogo);
                intent.putExtra("data", mData);
                intent.putExtra("immagine", mImage);
                startActivity(intent);
            }


        }));
    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento.date_collection_arr.remove(eve);
        eventi.remove(doc);

        adapter = new AdaptEvento(getContext(), eventi, itemClickListener);
        listaEventiView.setAdapter(adapter);
    }

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
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
                    if (photoUser.getPhotoUrl() != null) {
                        Glide.with(getContext()).load(photoUser.getPhotoUrl()).into(image_profile);
                    }
                    image_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(60, 60)
                                    .start(getContext(),ProfileFragment.this);
                        }
                    });

                fullname.setText(user.getFullname());
                category.setText(user.getCategory());


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
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user.getPhotoUrl() != null) {
                            Glide.with(getContext()).load(user.getPhotoUrl()).into(image_profile);

                        }
                    }
                }
            });
        }
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DatabaseEvento post = snapshot.getValue(DatabaseEvento.class);
                    if (post.getTitolo().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myFotos(){

    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        FirebaseUser nome = firebaseAuth.getCurrentUser();
        String nome1= nome.getDisplayName().replaceAll("%20" ," ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("UserID").child("Utenti").child(nome1).child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Eventi");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DatabaseEvento post = snapshot.getValue(DatabaseEvento.class);

                    for (String id : mySaves) {

                        if (post.getTitolo().equals(id)) {
                            eventi.add(post);
                        }
                    }
                    //eventi.add(post);


                }

                adapter = new AdaptEvento(getContext(), eventi, itemClickListener);

                //listaEventiView.setAdapter(adapter);
                listaEventiView.setAdapter(new AdaptEvento(getContext(), eventi, new AdaptEvento.OnItemClickListener() {
                    @Override public void onItemClick(DatabaseEvento item) {

                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mData = item.getDate();
                        String mImage = item.getImmagine();
                        Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliEvento.class);
                        intent.putExtra("titolo", mTitolo);
                        intent.putExtra("luogo", mLuogo);
                        intent.putExtra("data", mData);
                        intent.putExtra("immagine", mImage);
                        startActivity(intent);
                    }


                }));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
