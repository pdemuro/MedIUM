package com.medium.progettomedium;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medium.progettomedium.ActivityDettagliEvento;
import com.medium.progettomedium.Adapter.AdaptEvento;
import com.medium.progettomedium.Adapter.MyFotosAdapter;
import com.medium.progettomedium.Adapter.PostAdapter;
import com.medium.progettomedium.Model.DatabaseEvento;
import com.medium.progettomedium.Model.Post;
import com.medium.progettomedium.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ActivityPostDetail extends AppCompatActivity {

    String postid;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
private PostAdapter adapter;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private RecyclerView listaEventiView;
    DatabaseReference mRef;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_detail);

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");

        //Inflate the layout for this fragment
        listaEventiView = (RecyclerView) findViewById(R.id.recycler_view);
        listaEventiView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listaEventiView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<Post>();
        postAdapter = new PostAdapter(ActivityPostDetail.this, postList);
        listaEventiView.setAdapter(postAdapter);



        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");

        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

       readPost();

    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
