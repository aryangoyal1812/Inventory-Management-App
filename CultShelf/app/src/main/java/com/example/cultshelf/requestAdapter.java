package com.example.cultshelf;


import android.content.Context;
import android.content.Intent;
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

import com.example.cultshelf.modals.Request;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class requestAdapter extends FirestoreRecyclerAdapter<Request, requestAdapter.requestViewHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     *
     */
    public requestAdapter(@NonNull FirestoreRecyclerOptions<Request> options) {
        super(options);
    }
    Context TAG;
    String newOwn = "NULL";
    @Override
    protected void onBindViewHolder(@NonNull final requestViewHolder holder, final int position, @NonNull final Request model) {

        holder.productName.setText(model.ProductName);
        holder.productClub.setText(model.ClubName);
        //String newOwnerName = username(model.RequestedBy);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(model.RequestedBy).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("NotificationsActivity", "DocumentSnapshot data: " + document.getData());
                        newOwn =  document.getString("displayName");
                        holder.requestedBy.setText(newOwn);
                    } else {
                        Log.d("NotificationsActivity", "No such document");
                    }
                } else {
                    Log.d("NotificationsActivity", "get failed with ", task.getException());
                }
            }
        });


        holder.currentOwner.setText(model.CurrentOwner);
        //case 1 coordi buttons will be accept or cancel the request and changes in the product Activity respectively
        //case2 normal user button will be cancel only and respective changes in the product activity;
        //TODO : CheckSituation();
        checkSituation(model,holder);
        holder.cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.requestButton.setEnabled(false);
                TAG = v.getContext();
                //holder.requestButton.setEnabled(false);
                //Toast.makeText(TAG,"Navigate to notifications to check status of the request",Toast.LENGTH_SHORT).show();
                CancelProductRequest(model);
            }
        });
        Log.v("NotificationsActivity", newOwn);
        //TODO :AcceptProductRequest();
        holder.accept_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = v.getContext();
                AcceptProductRequest(model);
            }
        });
    }

    private void checkSituation(Request model,requestViewHolder holder) {
        if(!model.RequestStatus.equals("Incoming")){
            holder.accept_Button.setEnabled(false);
            holder.accept_Button.setVisibility(View.GONE);
        }
    }

    private void AcceptProductRequest(final Request model) {
        if(model.RequestStatus.equals("Incoming")){
            //final String newOwnerName = newOwn;//username(model.RequestedBy);
            final CollectionReference productList = FirebaseFirestore.getInstance().collection("Clubs").document(model.ClubName).collection("Products");
            productList
                    .whereEqualTo("ProductName", model.ProductName)//.whereEqualTo("RequestStatus","Incoming")
                    //.whereEqualTo("RequestedBy",model.RequestedBy)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("NotificationsActivity", document.getId() + " => " + document.getData());
                                    productList.document(document.getId()).update("ProductOwner",newOwn)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("NotificationsActivity", "DocumentSnapshot successfully updated!");

                                            //TODO:MAKE TEXT
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("NotificationsActivity", "Error updating document", e);
                                                }
                                            });
                                }
                            } else {
                                Log.d("NotificationsActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference clicker = db.collection("users").document(model.RequestedBy).collection("Request");
            clicker
                    .whereEqualTo("ProductName", model.ProductName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("NotificationsActivity", document.getId() + " => " + document.getData());
                                    clicker.document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("NotificationsActivity", "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("NotificationsActivity", "Error deleting document", e);
                                                }
                                            });
                                    requestAdapter.this.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("NotificationsActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            CollectionReference receiver = FirebaseFirestore.getInstance().collection("Clubs").document(model.ClubName).collection("Club Coordinators");
            final CollectionReference userreceiver = FirebaseFirestore.getInstance().collection("users");
            receiver
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("ProductActivity", document.getId() + " is the => " + document.getData().get("emailId") + "type" + document.getClass().getSimpleName());

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
                                                                    //Request request = new Request(model.ProductName,model.ClubName,"Incoming",user.getUid(),model.ProductOwner);
                                                                    //userreceiver.document(document.getId()).collection("Request").document().set(request);
                                                                    final CollectionReference coordinator =  userreceiver.document(document.getId()).collection("Request");
                                                                    coordinator
                                                                            .whereEqualTo("ProductName", model.ProductName)
                                                                            .whereEqualTo("RequestedBy",model.RequestedBy)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("NotificationsActivity", document.getId() + " => " + document.getData());
                                                                                            coordinator.document(document.getId())
                                                                                                    .delete()
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            Log.d("NotificationsActivity", "DocumentSnapshot successfully deleted!");
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Log.w("NotificationsActivity", "Error deleting document", e);
                                                                                                        }
                                                                                                    });
                                                                                            requestAdapter.this.notifyDataSetChanged();
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("NotificationsActivity", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                                requestAdapter.this.notifyDataSetChanged();
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
    }

    void CancelProductRequest(final Request model){
        if(model.RequestStatus.equals("Want") || model.RequestStatus.equals("Incoming")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final CollectionReference clicker = db.collection("users").document(model.RequestedBy).collection("Request");
            clicker
                    .whereEqualTo("ProductName", model.ProductName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("NotificationsActivity", document.getId() + " => " + document.getData());
                                    clicker.document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("NotificationsActivity", "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("NotificationsActivity", "Error deleting document", e);
                                                }
                                            });
                                    requestAdapter.this.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("NotificationsActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            CollectionReference receiver = db.collection("Clubs").document(model.ClubName).collection("Club Coordinators");
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
                                                                    //Request request = new Request(model.ProductName,model.ClubName,"Incoming",user.getUid(),model.ProductOwner);
                                                                    //userreceiver.document(document.getId()).collection("Request").document().set(request);
                                                                    final CollectionReference coordinator =  userreceiver.document(document.getId()).collection("Request");
                                                                    coordinator
                                                                            .whereEqualTo("ProductName", model.ProductName)
                                                                            .whereEqualTo("RequestedBy",model.RequestedBy)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("NotificationsActivity", document.getId() + " => " + document.getData());
                                                                                            coordinator.document(document.getId())
                                                                                                    .delete()
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            Log.d("NotificationsActivity", "DocumentSnapshot successfully deleted!");
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Log.w("NotificationsActivity", "Error deleting document", e);
                                                                                                        }
                                                                                                    });
                                                                                            requestAdapter.this.notifyDataSetChanged();
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("NotificationsActivity", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                                requestAdapter.this.notifyDataSetChanged();
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
    }

    @NonNull
    @Override
    public requestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        Log.d("Notification","Hi");
        return new requestViewHolder(viewHolder);
    }

    public class requestViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productClub,currentOwner,requestedBy;
        Button cancel_Button,accept_Button;
        public requestViewHolder(@NonNull View requestView){
            super(requestView);
            Log.d("Notification","Hi");
            productName = requestView.findViewById(R.id.productName);
            productClub = requestView.findViewById(R.id.productClub);
            requestedBy = requestView.findViewById(R.id.requestedBy);
            requestedBy = requestView.findViewById(R.id.requestedBy);
            currentOwner = requestView.findViewById(R.id.currentOwner);
            cancel_Button = requestView.findViewById(R.id.cancel_button);
            accept_Button = requestView.findViewById(R.id.accept_button);
        }


    }

//    String username(String uid){
//        final String[] newOwner = new String[1];
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d("NotificationsActivity", "DocumentSnapshot data: " + document.getData());
//                        newOwn =  document.getString("displayName");
//
//                    } else {
//                        Log.d("NotificationsActivity", "No such document");
//                    }
//                } else {
//                    Log.d("NotificationsActivity", "get failed with ", task.getException());
//                }
//            }
//        });
//        Log.v("NotificationsActivity", newOwner[0]);
//        return newOwner[0];
//
//    }


}
