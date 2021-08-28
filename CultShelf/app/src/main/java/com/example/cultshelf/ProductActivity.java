package com.example.cultshelf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Product;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductActivity extends AppCompatActivity {

    private productAdapter adapter;
    String clubName;

    List<DocumentSnapshot> coordinators;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //geetind culb name using getintent
        Intent intent = getIntent();
        clubName = intent.getStringExtra("ClubName");
        getSupportActionBar().setTitle(clubName);

        //starting recyclerView
        setUprecyclerView(clubName);

        //fetching email id of coordinator of club add in an array
        final FloatingActionButton fab = findViewById(R.id.add_product_button);
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore dbcoor = FirebaseFirestore.getInstance();
        CollectionReference df = dbcoor.collection("Clubs").document(clubName).collection("Club Coordinators");
        df.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //coordinators=new ArrayList<String>();
                coordinators=queryDocumentSnapshots.getDocuments();

                for(int i = 0;i<coordinators.size();++i){
                    Log.v("RareActivity", (coordinators.get(i).getString("emailId"))+ "K");
                    if(Objects.equals(coordinators.get(i).getString("emailId"), currentUser)){
                        fab.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                //System.out.println(coordinators);





            }
        });


    }


    public void addProduct(View view) {
        Intent intent = new Intent(this,CreateProductActivity.class);
        intent.putExtra("ProductClub",clubName);
        startActivity(intent);
    }


    public void setUprecyclerView( String clubNamecopy){


        Query query = FirebaseFirestore.getInstance().collection("Clubs").document(clubNamecopy).collection("Products");//.orderBy("ProductName", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Product> recyclerViewOptions = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        adapter = new productAdapter(recyclerViewOptions);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.product_recyclerView);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}