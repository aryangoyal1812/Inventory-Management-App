package com.example.cultshelf.daos;

import com.example.cultshelf.modals.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDaos {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usercollection = db.collection("users");

    public void adduser(User user){
        if(user!=null){
            usercollection.document(user.uid).set(user);
        }
    }
}
