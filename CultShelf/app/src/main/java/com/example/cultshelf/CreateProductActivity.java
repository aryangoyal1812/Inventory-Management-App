package com.example.cultshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.cultshelf.daos.ProductDaos;

public class CreateProductActivity extends AppCompatActivity {
    public String ProductClub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        getSupportActionBar().setTitle("Add Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        ProductClub = intent.getStringExtra("ProductClub");
    }

    public void createProduct(View view){
        EditText productName,productOwner,productDes;
        String ProductName,ProductOwner,ProductDes;
        productName = (EditText)findViewById(R.id.productName);
        productOwner = (EditText)findViewById(R.id.productOwner);

        ProductName = productName.getText().toString().trim();
        ProductOwner = productOwner.getText().toString().trim();

        if(!ProductName.isEmpty() && !ProductOwner.isEmpty() ){
            ProductDaos productDaos = new ProductDaos();
            //productDaos.setProductClub(ProductClub);
            productDaos.addproduct(ProductClub,ProductName,ProductOwner);
            finish();
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