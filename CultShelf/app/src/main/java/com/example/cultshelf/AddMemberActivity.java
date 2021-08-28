package com.example.cultshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Coordinator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class AddMemberActivity extends AppCompatActivity {
    String clubName;
    private CoordinatorAdapter coorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        clubName = intent.getStringExtra("ClubName");
        getSupportActionBar().setTitle(clubName + " Coordinators");
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_AddCoordinators);
        setUpFab(fab,currentUser);
        setUpCoordinatorrecyclerView(clubName);
    }



    public void fab_AddCordinators(View view) {
        Intent intent = new Intent(this,CreateCoordinatorsActivity.class);
        intent.putExtra("Club",clubName);
        startActivity(intent);
    }


    public void setUpCoordinatorrecyclerView(String CoordinatorClub){


        Query query = FirebaseFirestore.getInstance().collection("Clubs").document(CoordinatorClub).collection("Club Coordinators");
        FirestoreRecyclerOptions<Coordinator> recyclerViewOptions = new FirestoreRecyclerOptions.Builder<Coordinator>().setQuery(query, Coordinator.class).build();
        coorAdapter = new CoordinatorAdapter(recyclerViewOptions);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.Coordinator_recyclerView);

        recyclerView.setAdapter(coorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setUpFab(final FloatingActionButton fab, final String currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference secretaryCollection = db.collection("Secretary");
        secretaryCollection
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ProductActivity", document.getId() + " is the => " + document.getData().get("emailId") + "type" + document.getClass().getSimpleName());
                                //document.get
                                //Log.d("ProductActivity", document.getId() + " is the => " + type(document.getData()));
                                // Iterate the map using
                                // for-each loop
                                for (Map.Entry<String, Object> e : document.getData().entrySet()){

                                    Log.d("ProductActivity","Key: " + e.getKey()
                                            + " Value: " + e.getValue());
                                    if(e.getValue().equals(currentUser)){

                                        fab.setVisibility(View.VISIBLE);
                                        break;
                                    }
//                                    else{
//                                        fab.setVisibility(View.GONE);
//                                    }
                                }


                            }
                        } else {
                            Log.d("ProductActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        coorAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        coorAdapter.stopListening();
    }
}