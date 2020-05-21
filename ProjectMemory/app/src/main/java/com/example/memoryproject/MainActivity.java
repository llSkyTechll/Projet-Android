package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.memoryproject.Notification.NotifWorker;

public class MainActivity extends AppCompatActivity {

    Button btnPlay;
    Boolean isPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPlay = false;
        btnPlay = findViewById(R.id.btn_play);
        setListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isPlay){
            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(NotifWorker.class)
                    .build();
            WorkManager.getInstance(this).enqueue(uploadWorkRequest);
        }
    }

    private void setListener(){
        btnPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isPlay = true;
                relocateSelectGrid();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPlay = false;
    }

    private void relocateSelectGrid(){
        isPlay = true;
        Intent gridChoiceIntent = new Intent(this, GridChoice.class);
        startActivity(gridChoiceIntent);
    }
}
