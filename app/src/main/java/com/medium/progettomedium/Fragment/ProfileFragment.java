package com.medium.progettomedium.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.medium.progettomedium.ActivityDettagliAmm;
import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.ActivityModificaEvento;
import com.medium.progettomedium.Adapter.AdaptEventoModificabile;
import com.medium.progettomedium.Adapter.AdaptEventoUtente;
import com.medium.progettomedium.Adapter.MyFotosAdapter;
import com.medium.progettomedium.AddEventoActivity2;
import com.medium.progettomedium.EditProfileActivity;
import com.medium.progettomedium.LoginActivity;
import com.medium.progettomedium.MainActivity;
import com.medium.progettomedium.MapActivity;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.DatabaseUtente;
import com.medium.progettomedium.Model.Post;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CardView informazioni;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


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
     //   my_fotos = view.findViewById(R.id.my_fotos);

        iMieiPost = view.findViewById(R.id.iMieiPost);
        iMieiEventi = view.findViewById(R.id.iMieiEventi);
        informazioni = view.findViewById(R.id.informazioni);

      //  saved_fotos = view.findViewById(R.id.saved_fotos);
        logout=view.findViewById(R.id.logout);
        options = view.findViewById(R.id.options);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myFotosAdapter = new MyFotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotosAdapter);
        postList_saves = new ArrayList<>();


        listaEventiView = (RecyclerView) view.findViewById(R.id.recycler_view_save);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        eventi = new ArrayList<DatabaseEvento>();
        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        userInfo();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
        databaseReferenceutente = FirebaseDatabase.getInstance().getReference();
        databaseReferenceutente.child("UserID").child("Utenti").child(nome1).child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue().equals("Utente")) {
                    myFotos();
                    postEvent();
                    mySaves();

                    iMieiPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerView.setVisibility(View.VISIBLE);
                            listaEventiView.setVisibility(View.GONE);
                            iMieiPost.setCardBackgroundColor(ContextCompat.getColor(getActivity()/*context*/, (R.color.material_blue_grey_100)));
                            iMieiEventi.setCardBackgroundColor(ContextCompat.getColor(getActivity()/*context*/, (R.color.colorWhite)));
                            myFotos();
                        }
                    });


                    iMieiEventi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerView.setVisibility(View.GONE);
                            listaEventiView.setVisibility(View.VISIBLE);
                            iMieiEventi.setCardBackgroundColor(ContextCompat.getColor(getActivity()/*context*/, (R.color.material_blue_grey_100)));
                            iMieiPost.setCardBackgroundColor(ContextCompat.getColor(getActivity()/*context*/, (R.color.colorWhite)));
                            postEvent();
                        }
                    });


                }else{
                    postEvent();
                    listaEventiView.setVisibility(View.GONE);
                    iMieiEventi.setVisibility(View.GONE);
                    iMieiPost.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    informazioni.setVisibility(View.VISIBLE);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


        //getFollowers();
        //getNrPosts();


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

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                // Setting Dialog Title
                //builder.setTitle("Internet non disponibile");

                // Setting Dialog Message
                builder.setMessage("Sei sicuro di voler uscire Dal profilo?");

                // On pressing the Settings button.
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                       // Toast.makeText(getContext(),"Modifica annullata",Toast.LENGTH_SHORT).show();



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
    private void myFotos(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(nome1)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void postEvent(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("Eventi");
        //POPOLAZIONE EVENTI DA DATABASE
        databaseReference.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeData(dataSnapshot);
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            public void onCancelled(DatabaseError databaseError) {
            }


        });
    }

    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        final DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        final DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String nome1= user.getDisplayName().replaceAll("%20" ," ");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        databaseReference3 = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("UserID").child("Utenti").child(nome1).addValueEventListener(new ValueEventListener() {

            /**
             * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                DatabaseUtente databaseUtente = dataSnapshot.getValue(DatabaseUtente.class);
                String tipo = databaseUtente.getCategory();
                if(tipo.equals("Utente")){
                    databaseReference2.child("UserID").child("Utenti").child(nome1).child("prenotazioni").addValueEventListener(new ValueEventListener() {

                        /**
                         * This method will be invoked any time the data on the database changes.
                         * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get all of the children at this level.
                            DatabaseUtente databaseUtente = dataSnapshot.getValue(DatabaseUtente.class);
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            // shake hands with each of them.'
                            int var = 0;
                            for (DataSnapshot child : children) {

                                if (doc.getId().equals(child.getKey())) {
                                    var = 1;
                                }

                            }
                            if (var == 1) {
                                DatabaseEvento.date_collection_arr.add(eve);
                                eventi.add(doc);
                            }
                            adapterutente = new AdaptEventoUtente(getContext(), eventi, itemClickListenerutente);

                            //listaEventiView.setAdapter(adapter);
                            listaEventiView.setAdapter(new AdaptEventoUtente(getContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
                                @Override
                                public void onItemClick(DatabaseEvento item) {

                                    String mTitolo = item.getTitolo();
                                    String mLuogo = item.getLuogo();
                                    String mDescrizione = item.getDescrizione();
                                    String mImage = item.getImmagine();
                                    String mData = item.getDate();
                                    String mId = item.getId();
                                    Intent intent = new Intent(recyclerView.getContext(), ActivityDettagliEvento.class);
                                    intent.putExtra("title", mTitolo);
                                    intent.putExtra("description", mLuogo);
                                    intent.putExtra("descrizione", mDescrizione);
                                    intent.putExtra("image", mImage);
                                    intent.putExtra("date", mData);
                                    intent.putExtra("id", mId);
                                    startActivity(intent);
                                }


                            }));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    databaseReference3.child("UserID").child("Utenti").child(nome1).child("Eventi Creati").addValueEventListener(new ValueEventListener() {

                        /**
                         * This method will be invoked any time the data on the database changes.
                         * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                         *
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get all of the children at this level.
                            DatabaseUtente databaseUtente = dataSnapshot.getValue(DatabaseUtente.class);
                            String tipo = databaseUtente.getCategory();
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            // shake hands with each of them.'
                            int var = 0;
                            for (DataSnapshot child : children) {

                                if (doc.getId().equals(child.getKey())) {
                                    var = 1;
                                }

                            }
                            if (var == 1) {
                                DatabaseEvento.date_collection_arr.add(eve);
                                eventi.add(doc);
                            }
                            adaptEventoModificabile = new AdaptEventoModificabile(getContext(), eventi, itemClickListener2);

                            //listaEventiView.setAdapter(adapter);
                            listaEventiView.setAdapter(new AdaptEventoModificabile(getContext(), eventi, new AdaptEventoModificabile.OnItemClickListener() {
                                @Override
                                public void onItemClick(DatabaseEvento item) {

                                }


                            }));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        eventi.clear();
    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

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

                Glide.with(getContext()).load(user.getImageUrl()).into(image_profile);

                image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(60, 60)
                                .start(getContext(),ProfileFragment.this);


                    }
                });

                String nome = user.getNome()+" "+user.getCognome();
                fullname.setText(nome);
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

                adapterutente = new AdaptEventoUtente(getContext(), eventi, itemClickListenerutente);

                //listaEventiView.setAdapter(adapter);
                listaEventiView.setAdapter(new AdaptEventoUtente(getContext(), eventi, new AdaptEventoUtente.OnItemClickListener() {
                    @Override public void onItemClick(DatabaseEvento item) {

                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mDescrizione = item.getDescrizione();
                        String mImage = item.getImmagine();
                        String mData= item.getDate();
                        String mId = item.getId();
                        Intent intent = new Intent(recyclerView.getContext(), ActivityDettagliEvento.class);
                        intent.putExtra("title", mTitolo);
                        intent.putExtra("description", mLuogo);
                        intent.putExtra("descrizione", mDescrizione);
                        intent.putExtra("image", mImage);
                        intent.putExtra("date", mData);
                        intent.putExtra("id",mId);
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
