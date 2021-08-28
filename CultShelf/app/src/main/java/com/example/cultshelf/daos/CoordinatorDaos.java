package com.example.cultshelf.daos;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Coordinator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CoordinatorDaos {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void addCoordinators(String CoordinatorClub ,String name ,String emailid){
        CollectionReference clubCollection = db.collection("Clubs").document(CoordinatorClub).collection("Club Coordinators");
        Coordinator c = new Coordinator();
        c.name = name;
        c.emailId = emailid;
        clubCollection.document(emailid).set(c);

    }

}
