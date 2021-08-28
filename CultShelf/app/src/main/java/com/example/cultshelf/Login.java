package com.example.cultshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cultshelf.daos.ClubDao;
import com.example.cultshelf.daos.UserDaos;
import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Request;
import com.example.cultshelf.modals.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private clubAdapter adapter;
    private ClubDao clubdao;
    private static final String TAG = "Login";
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    String currentUser;
    FloatingActionButton fab;

    TextView userName,userEmail,userId;

    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Clubs");
        invalidateOptionsMenu();
        userName = findViewById(R.id.user_name);
//        userEmail = findViewById(R.id.email);
       //userId = findViewById(R.id.userId);
        String displayName = null;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedpreferences = getSharedPreferences("SpecialKey",Context.MODE_PRIVATE);

        displayName = sharedpreferences.getString("name","Name not Found");

        userName.setText(displayName);
//        userEmail.setText(sharedpreferences.getString("email","Email not found"));

//        Log.v(TAG,displayName);
        //Log.v(TAG,user.getEmail());

        User u  = new User();
        u.uid = user.getUid();
        u.displayName = user.getDisplayName();
        u.emailId= sharedpreferences.getString("email","Email not found");
        UserDaos userdao = new UserDaos();
        userdao.adduser(u);
        currentUser = user.getEmail();
        fab = (FloatingActionButton)findViewById(R.id.fab);

        //Extracting secretary email from firestore;
        //matching with current user
//
//        FirebaseFirestore dbsec = FirebaseFirestore.getInstance();
//        DocumentReference df = dbsec.collection("Secretary").document("CultSecretary");
//        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.getString("Sec_Email").equals(currentUser)){
//                    //fab(fab);
//                    fab.setVisibility(View.VISIBLE);
//
//                }else{
//                    fab.setVisibility(View.GONE);
//                }
//
//            }
//        });
        setUpFab(fab,currentUser);
        


        setUprecyclerView();




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


    private void signOut(){
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       // updateUI(null);
                        //TODO : What happens when we log out
                        gotoMainActivity();
                    }
                });
        Toast.makeText(Login.this,"Logged out",Toast.LENGTH_SHORT).show();
    }


    private void gotoMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_button:
                //AuthUI.getInstance().signOut(this);
                signOut();
                return true;
            case R.id.bell_button:
                Intent intent = new Intent(this,NotificationsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    public void fab(View view) {
        Intent intent = new Intent(this,CreateClubActivity.class);
        startActivity(intent);
    }



    public void setUprecyclerView(){


        Query query = FirebaseFirestore.getInstance().collection("Clubs").orderBy("clubname", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Club> recyclerViewOptions = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, Club.class).build();
        adapter = new clubAdapter(recyclerViewOptions);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }

//    private void HandleNotificationBell() {
//        //final MenuItem settingsItem = menu.findItem(R.id.bell_button);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        assert user != null;
//        CollectionReference query = FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("Request");
//        query
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                if (document != null) {
//                                    Log.d(TAG, "DocumentSnapshot data: " + task.getResult());
//                                    //Do the registration
//
//                                    Drawable mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_notifications_24);
//                                    //ettingsItem.setIcon(mDrawable);
//                                } else {
//                                    Log.d(TAG, "No such document");
//
//                                    Drawable mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_notifications_24);
//                                    //settingsItem.setIcon(mDrawable);
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


//    }

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



