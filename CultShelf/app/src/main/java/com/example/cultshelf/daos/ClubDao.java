package com.example.cultshelf.daos;

import android.net.Uri;
import android.widget.ImageView;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class ClubDao {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference clubcollection = db.collection("Clubs");

    public void addclub(String club,String imageUrl){
        Club c = new Club();
        c.clubname = club;
        c.logoRef= imageUrl;
        clubcollection.document(club).set(c);

    }
}
