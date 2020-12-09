package com.example.cultshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cultshelf.daos.ClubDao;

public class CreateClubActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club2);





    }



    public void createClub(View view) {
        EditText clubName= (EditText)findViewById(R.id.clubName);
        String input = clubName.getText().toString().trim();
        if(!input.isEmpty()){
            ClubDao clubDao = new ClubDao();
            clubDao.addclub(input);
            finish();
        }
    }
}