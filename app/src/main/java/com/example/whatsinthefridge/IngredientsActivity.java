package com.example.whatsinthefridge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

public class IngredientsActivity extends AppCompatActivity {

    @SuppressLint("NewApi")
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


// Initialize the Vertex AI service and the generative model
// Specify a model that supports your use case
// Gemini 1.5 models are versatile and can be used with all API capabilities
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash-preview-0514");

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
GenerativeModelFutures model = GenerativeModelFutures.from(gm);

// Provide a prompt that contains text
        Content prompt = new Content.Builder()
                .addText("Write a story about a magic backpack.")
                .build();

// To generate text output, call generateContent with the text input
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.e("alma",resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, this.getApplicationContext().getMainExecutor());

        // דוגמה להגדרת טקסט התחלתי
        editText.setHint("הכנס טקסט כאן...");
    }

    }
