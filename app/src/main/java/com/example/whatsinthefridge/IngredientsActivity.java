package com.example.whatsinthefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredients);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button by its ID
        Button recipeButton = findViewById(R.id.recipeButton);

        // Set click listener for the button
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the RecipeListActivity
                Intent intent = new Intent(IngredientsActivity.this, RecipeListActivity.class);
                startActivity(intent);
            }
        });

        // מציאת ה-EditText לפי ה-ID
        EditText editText = findViewById(R.id.editText);
        Button submitButton = findViewById(R.id.submitButton);

        // הגדרת לחיצה על כפתור להצגת הטקסט
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                Toast.makeText(IngredientsActivity.this, "הטקסט שהוזן: " + userInput, Toast.LENGTH_SHORT).show();
            }
        });

        // דוגמה להגדרת טקסט התחלתי
        editText.setHint("הכנס טקסט כאן...");
    }

    }
