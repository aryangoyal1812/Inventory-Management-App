package com.example.cultshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Product;
import com.example.cultshelf.modals.Request;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class productAdapter extends FirestoreRecyclerAdapter<Product, productAdapter.productViewHolder> {
    public productAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }
    Context TAG;
    @Override
    protected void onBindViewHolder(@NonNull final productAdapter.productViewHolder holder, int position, @NonNull final Product model) {
        holder.productName.setText(model.ProductName);
        holder.productOwner.setText(model.ProductOwner);
        Log.d("ProductActivity", " is the Hi2");
        CheckSituation(model,holder);
        holder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.requestButton.setEnabled(false);
                TAG = v.getContext();
                //holder.requestButton.setEnabled(false);
                Toast.makeText(TAG,"Navigate to notifications to check status of the request",Toast.LENGTH_SHORT).show();
                SendProductRequest(model);
            }
        });
    }

    private void CheckSituation(final Product model, @NonNull final productAdapter.productViewHolder holder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference clicker = db.collection("users").document(user.getUid()).collection("Request");
        clicker.
                whereEqualTo("ProductName",model.ProductName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()  && !task.getResult().isEmpty()) {
                            Log.d("ProductActivity", "Hi" +  task.getResult().isEmpty());
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("ProductActivity", document.getId() + " => " + document.getData());
                                Request request = document.toObject(Request.class);
                                if(request.RequestStatus.equals("Want")){

                                    Log.d("ProductActivity", " HiHello2");
                                    holder.requestButton.setText("Request-Sent");
                                    holder.requestButton.setBackgroundColor(Color.RED);
                                    holder.requestButton.setEnabled(false);
                                    //Toast.makeText(TAG,"Navigate to notifications to check status of the request",Toast.LENGTH_SHORT).show();

                                }
                                //if(clicker.document(document.getId()).get("RequestStatus").equals(""))

                            }
                        } else {
                            Log.d("ProductActivity", " HiHello3" + model.ProductName + task.getResult().isEmpty() + task.isSuccessful());
                            holder.requestButton.setText("Ask For Product");
                            holder.requestButton.setBackgroundColor(Color.GREEN);
                            holder.requestButton.setEnabled(true);
                            Log.d("ProductActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void SendProductRequest(final Product model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference sender = db.collection("users").document(user.getUid());
        Request request = new Request(model.ProductName,model.ProductClub,"Want",user.getUid(),model.ProductOwner);
        sender.collection("Request").document().set(request);
        Log.d("ProductActivity", " is the Hi" + model.ProductClub);
        CollectionReference receiver = db.collection("Clubs").document(model.ProductClub).collection("Club Coordinators");
        final CollectionReference userreceiver = db.collection("users");
        receiver
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
                                    if(e.getKey().equals("emailId")){
                                        userreceiver
                                                .whereEqualTo("emailId", e.getValue())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                Log.d("ProductActivity", document.getId() + " => " + document.getData());
                                                                Request request = new Request(model.ProductName,model.ProductClub,"Incoming",user.getUid(),model.ProductOwner);
                                                                userreceiver.document(document.getId()).collection("Request").document().set(request);
                                                            }
                                                            productAdapter.this.notifyDataSetChanged();
                                                        } else {
                                                            Log.d("ProductActivity", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                }


                            }
                        } else {
                            Log.d("ProductActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @NonNull
    @Override
    public productAdapter.productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        TAG = parent.getContext();
        return new productAdapter.productViewHolder(viewHolder);

    }

    public class productViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productOwner,productDes;
        Button requestButton;
        public productViewHolder(@NonNull View productView){
            super(productView);
            productName = productView.findViewById(R.id.productName);
            productOwner = productView.findViewById(R.id.productOwner);
            requestButton = productView.findViewById(R.id.requestButton);
        }
    }


}
