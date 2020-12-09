package com.example.cultshelf.daos;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubDao {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference clubcollection = db.collection("Clubs");

    public void addclub(String club){
        Club c = new Club();
        c.clubname = club;
        clubcollection.document(club).set(c);

    }
}
