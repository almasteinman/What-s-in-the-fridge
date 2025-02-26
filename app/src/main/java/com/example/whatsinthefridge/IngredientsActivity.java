package com.example.whatsinthefridge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class IngredientsActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private Button voiceInputButton, generateRecipeButton;
    private SpeechRecognizer speechRecognizer;
    private GenerativeModelFutures model;

    ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.e("XXX", "✅ onActivityResult הופעל!");
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (resultList != null && !resultList.isEmpty()) {
                            String speechText = resultList.get(0);
                            Log.d("XXX", "🎤 טקסט מזיהוי דיבור: " + speechText);

                            // הצגת הטקסט ב-EditText
                            ingredientInput.setText(speechText);

                            // שליחת הטקסט ל-Gemini
                            sendToGemini(speechText);
                        }
                    }
                }
            });

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

        ingredientInput = findViewById(R.id.ingredientInput);
        voiceInputButton = findViewById(R.id.voiceInputButton);
        generateRecipeButton = findViewById(R.id.recipeButton);

        // יצירת אובייקט לזיהוי דיבור
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        voiceInputButton.setOnClickListener(v -> startSpeechToText());

        // אתחול מודל Gemini
        GenerativeModel gm = FirebaseVertexAI.getInstance().generativeModel("gemini-1.5-flash-preview-0514");
        model = GenerativeModelFutures.from(gm);

        // כפתור יצירת המתכון
        generateRecipeButton.setOnClickListener(v -> {
            String ingredients = ingredientInput.getText().toString();
            if (ingredients.isEmpty()) {
                Toast.makeText(this, "אנא הזן רכיבים ליצירת מתכון", Toast.LENGTH_SHORT).show();
            } else {
                sendToGemini(ingredients);
            }
        });
    }

    private void startSpeechToText() {
        Log.d("SpeechToText", "התחלתי את זיהוי הדיבור");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "תגיד את רכיבי המתכון");
        speechRecognitionLauncher.launch(intent);
    }

    private void sendToGemini(String text) {
        Log.d("GeminiAI", "שולח את הטקסט ל-Gemini: " + text);

        // בניית ה-Prompt עם הטקסט שנקלט מהדיבור
        Content prompt = new Content.Builder()
                .addText("Create 3 recipes using these ingredients: " + text)
                .build();

        // שליחת הבקשה ל-Gemini
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String generatedText = result.getText();
                Log.e("Gemini Response", generatedText);

                // מעבר למסך המתכונים עם התשובה מג'מיני
                sendDataToRecipeListActivity(generatedText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(IngredientsActivity.this, "שגיאה בשליחת הטקסט ל-Gemini", Toast.LENGTH_SHORT).show());
            }
        }, this.getMainExecutor());
    }

    private void sendDataToRecipeListActivity(String recipeData) {
        Intent intent = new Intent(IngredientsActivity.this, RecipeListActivity.class);
        intent.putExtra("recipe_data", recipeData);
        startActivity(intent);
    }
}


