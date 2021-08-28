package com.example.cultshelf.daos;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDaos {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();



    public void addproduct(String ProductClub,String ProductName,String ProductOwner){

        CollectionReference productCollection = db.collection("Clubs").document(ProductClub).collection("Products");
        Product p = new Product();
        p.ProductName = ProductName;
        p.ProductOwner = ProductOwner;
        p.ProductClub = ProductClub;
        productCollection.document().set(p);
        //clubcollection.document(club).set(c);

    }
}
