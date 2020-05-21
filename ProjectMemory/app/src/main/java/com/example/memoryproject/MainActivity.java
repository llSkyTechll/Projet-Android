package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.memoryproject.Notification.NotificationService;

public class MainActivity extends AppCompatActivity {

    Button btnPlay;
    private NotificationService notificationService;
    Boolean isPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPlay = false;
        notificationService = new NotificationService();
        btnPlay = findViewById(R.id.btn_play);
        setListener();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (!isPlay){
            notificationService.NotificationBuilder(this,"Memory project","Get back!!!");
        }
        isPlay = false;
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

    private void relocateSelectGrid(){
        Intent gridChoiceIntent = new Intent(this, GridChoice.class);
        startActivity(gridChoiceIntent);
    }
}
