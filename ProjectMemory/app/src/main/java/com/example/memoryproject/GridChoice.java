package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GridChoice extends AppCompatActivity {

    Button btn_2x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_choice);

        btn_2x2 = findViewById(R.id.btn_2x2);

        setListener();
    }

    private void setListener(){
        btn_2x2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relocateGrid2x2();
            }
        });
    }

    private void relocateGrid2x2(){
        //Intent grid2x2Intent = new Intent(this, /*2x2 Grid page?*/);
        //startActivity(grid2x2Intent);
    }
}
