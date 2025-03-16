package com.example.whatsinthefridge;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    TextView name, mail;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                            auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        auth = FirebaseAuth.getInstance();

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                                        // Create a custom User object
                                        User user = new User(
                                                userId,
                                                auth.getCurrentUser().getDisplayName(),
                                                auth.getCurrentUser().getEmail(),
                                                auth.getCurrentUser().getPhotoUrl() != null ? auth.getCurrentUser().getPhotoUrl().toString() : ""
                                        );

                                        Log.d("ALMA", "LoGIN --- "+ user.toString());
                                        // Save user info to Firebase Realtime Database
                                        //database.getReference("Users").child(userId).setValue(user);
//                                        database.getReference("Users").push().setValue(user)
//                                                .addOnSuccessListener(aVoid ->
//                                                        Toast.makeText(LogInActivity.this, "Data added successfully!", Toast.LENGTH_SHORT).show()
//                                                )
//                                                .addOnFailureListener(error ->
//                                                        Toast.makeText(LogInActivity.this, "Failed to add data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
//                                                );

                                        DatabaseReference usersRef = database.getReference("Users");

                                        usersRef.orderByChild("userId").equalTo(userId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            // User already exists, log and do nothing
                                                            Log.d("Firebase", "User already exists: " + userId);
                                                        } else {
                                                            // Create new user entry
                                                            database.getReference("Users").push().setValue(user)
                                                                    .addOnSuccessListener(aVoid ->
                                                                            Toast.makeText(LogInActivity.this, "Data added successfully!", Toast.LENGTH_SHORT).show()
                                                                    )
                                                                    .addOnFailureListener(error ->
                                                                            Toast.makeText(LogInActivity.this, "Failed to add data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                                                                    );
                                                        }
                                                    }

                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("Firebase", "Database error: " + error.getMessage());
                                                    }
                                                });



                                        // Load profile data
                                        Glide.with(LogInActivity.this)
                                                .load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl())
                                                .into(imageView);

                                        name.setText(auth.getCurrentUser().getDisplayName());
                                        mail.setText(auth.getCurrentUser().getEmail());

                                        Toast.makeText(LogInActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        intent.putExtra("USERNAME", auth.getCurrentUser().getDisplayName());
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LogInActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        imageView = findViewById(R.id.profileImage);
        name = findViewById(R.id.nameTV);
        mail = findViewById(R.id.mailTV);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LogInActivity.this, options);

        auth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        MaterialButton signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null) {
                            googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LogInActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LogInActivity.this, LogInActivity.class));
                                }
                            });
                        }
                    }
                });
                FirebaseAuth.getInstance().signOut();
            }
        });

        if (auth.getCurrentUser() != null) {
            Glide.with(LogInActivity.this)
                    .load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl())
                    .into(imageView);
            name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
        }
    }
}
