package com.example.whatsinthefridge;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    //AIRPLANE MODE
    AlertDialog noInternetDialog;
    AirplaneReceiver airplaneReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button by its ID
        Button ingredientsButton = findViewById(R.id.ingredientsButton);

        // Set click listener for the button
        ingredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the IngredientsActivity
                Intent intent = new Intent(MainActivity.this, IngredientsActivity.class);
                startActivity(intent);
            }
        });

        //GET USER from login
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        Log.d ("ALMA", "username = "+ username);
        //IF admin user - which is my user - alma
        //show one more button for display all user defined in app
        if (username.equals("Alma Steinman"))
        {
            MaterialButton listUser = findViewById(R.id.listUser);
            listUser.setVisibility(View.VISIBLE);
            listUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start the show all user activity
                    Intent intent = new Intent(MainActivity.this, ShowUsersActivity.class);
                    startActivity(intent);
                }
            });
        }

        //AIRPLANE MODE
        createDialog();
        airplaneReceiver = new AirplaneReceiver(noInternetDialog);
    }

    public void createDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AIRPLANE MODE INFORMATION");
        builder.setCancelable(true);
        builder.setPositiveButton("press ok to continue", null);
        noInternetDialog = builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //AIRPLANE MODE
        IntentFilter itfairplane = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneReceiver, itfairplane);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //AIRPLANE MODE
        unregisterReceiver(airplaneReceiver);
    }
}