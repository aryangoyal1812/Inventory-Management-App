package com.example.cultshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.cultshelf.modals.Product;
import com.example.cultshelf.modals.Request;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationsActivity extends AppCompatActivity {
    requestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUprecyclerView();
    }

    public void setUprecyclerView(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        Query query = FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("Request");//.orderBy("ProductName", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Request> recyclerViewOptions = new FirestoreRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        adapter = new requestAdapter(recyclerViewOptions);

        RecyclerView recyclerView = findViewById(R.id.request_recyclerView);
        recyclerView.setAdapter(adapter);


        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //HandleNotificationBell();
    }
//
//    private void HandleNotificationBell() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        assert user != null;
//        Query query = FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("Request");
//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(queryDocumentSnapshots.isEmpty()){
//                    Button BellButton = findViewById(R.id.bell_button);
//                    Drawable mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_notification_add_24);
//                    BellButton.setCompoundDrawablesWithIntrinsicBounds( mDrawable, null, null, null);
////                    Toast.makeText(getActivity(), "Collection is Empty",
////                            Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });
//    }

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