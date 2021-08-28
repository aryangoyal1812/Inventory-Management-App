package com.example.cultshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.cultshelf.daos.ClubDao;
import com.example.cultshelf.modals.Club;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class CreateClubActivity extends AppCompatActivity {

    private static final String TAG = "CreateClubActivity";
    private ImageView clubLogo;
    public Uri imageUri;

    private EditText clubName;
    private FirebaseStorage storage;
    private StorageReference storageref;
    private CollectionReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club2);
        getSupportActionBar().setTitle(" Add Club");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage=FirebaseStorage.getInstance();
        storageref =storage.getReference();
        db=FirebaseFirestore.getInstance().collection("Clubs");
        clubLogo = (ImageView) findViewById(R.id.club_logo);
        clubLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //method that open gallery to cloose logo
                ChooseLogo();

            }
        });





    }



    public void createClub(View view) {
        EditText clubName= (EditText)findViewById(R.id.clubName);
        String input = clubName.getText().toString().trim();

        if(!input.isEmpty()){
            UploadPicture(input);
            finish();
        }

    }

    private void  ChooseLogo(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            imageUri=data.getData();
            Glide.with(this).load(data.getData()).fitCenter().transform(new CircleCrop()).into(clubLogo);

            //UploadPicture();

        }
    }

    private void UploadPicture(final String cluv){
        if(imageUri!=null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.show();

            clubName = (EditText) findViewById(R.id.clubName);
            final String input = clubName.getText().toString().trim();

            final StorageReference riversRef = storageref.child(input);

            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Toast.makeText(this,"Uploaded Successfull",Toast.LENGTH_LONG).show();

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String download =uri.toString();
                                    ClubDao clubDao = new ClubDao();
                                    clubDao.addclub(cluv,download);


                                }
                            });
                            }

//                            if (taskSnapshot.getMetadata() != null) {
//                                if (taskSnapshot.getMetadata().getReference() != null) {
//                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
//                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            String imageUrl = uri.toString();
//                                            //createNewPost(imageUrl);
//                                            Club c = new Club();
//                                            c.logoRef = imageUrl;
//                                            db.document(clubName.toString()).set(c);
//                                        }
//                                    });
//                                }
//                            }



                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage(+(int) progressPercent + "%");
                        }
                    });


        }else{
            Toast.makeText(this,"No Image Selected",Toast.LENGTH_LONG).show();
        }
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
}