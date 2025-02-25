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

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;



public class IngredientsActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private Button voiceInputButton;
    private SpeechRecognizer speechRecognizer;

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

        // יצירת אובייקט לזיהוי דיבור
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        voiceInputButton.setOnClickListener(v -> startSpeechToText());

        // Find the button by its ID
        Button recipeButton = findViewById(R.id.recipeButton);

        // Set click listener for the button
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the IngredientsActivity
                Intent intent = new Intent(IngredientsActivity.this, RecipeListActivity.class);
                startActivity(intent);
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

        // אתחול המודל של Gemini
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash-preview-0514");

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // בניית ה-Prompt עם הטקסט שנקלט מהדיבור
        Content prompt = new Content.Builder()
                .addText("create a recipe with the following ingredients: " + text)
                .build();

        // שליחת הבקשה ל-Gemini
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.e("Gemini Response", resultText);
                runOnUiThread(() -> Toast.makeText(IngredientsActivity.this, "Gemini: " + resultText, Toast.LENGTH_LONG).show());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(IngredientsActivity.this, "שגיאה בשליחת הטקסט ל-Gemini", Toast.LENGTH_SHORT).show());
            }
        }, this.getApplicationContext().getMainExecutor());
    }
}


