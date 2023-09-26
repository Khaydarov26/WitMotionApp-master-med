package com.wit.example;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class db_view extends AppCompatActivity {

    my_database_helper myDb;
    ArrayList<String> accX, accY, accZ,time;
    CustomAdapter customAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_view);
        recyclerView = findViewById(R.id.recyclerView);
        myDb = new my_database_helper(db_view.this);

        accX = new ArrayList<>();
        accY = new ArrayList<>();
        accZ = new ArrayList<>();
        time = new ArrayList<>();

        storeDataInArray();
        customAdapter = new CustomAdapter(db_view.this, accX, accY, accZ,time);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(db_view.this));
    }

    void storeDataInArray(){
        Cursor cursor = myDb.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data...", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                accX.add(cursor.getString(0));
                accY.add(cursor.getString(1));
                accZ.add(cursor.getString(2));
                time.add(cursor.getString(3));
            }
        }
    }
}