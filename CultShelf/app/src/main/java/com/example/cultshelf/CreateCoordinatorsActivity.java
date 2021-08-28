package com.example.cultshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.cultshelf.daos.CoordinatorDaos;

public class CreateCoordinatorsActivity extends AppCompatActivity {

    public String coordinatorClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coordinators);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        coordinatorClub = intent.getStringExtra("Club");

        getSupportActionBar().setTitle("Add Coordinators");


    }

    public void AddCoordinator(View view) {

        EditText coorName = (EditText)findViewById(R.id.coordinator_name);
        EditText coorEmail= (EditText)findViewById(R.id.coordinator_Email_Id);
        String name = coorName.getText().toString().trim();
        String emailId = coorEmail.getText().toString().trim();

        if(!name.isEmpty() && !emailId.isEmpty()){
            CoordinatorDaos cd = new CoordinatorDaos();
            cd.addCoordinators(coordinatorClub,name,emailId);
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