package com.example.whatsinthefridge;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView name, mail;
    ShapeableImageView imageView;


   /* @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()) {
            auth = FirebaseAuth.getInstance();
            Glide.with(LogInActivity.this).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(imageView);
            name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
            Toast.makeText(LogInActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LogInActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        imageView = findViewById(R.id.profileImage);
        name = findViewById(R.id.nameTV);
        mail = findViewById(R.id.mailTV);
    }
}
