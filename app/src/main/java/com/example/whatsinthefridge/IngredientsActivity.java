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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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


public class IngredientsActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private Button voiceInputButton;
    private SpeechRecognizer speechRecognizer;

    ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.e("XXX", "onActivityResult");
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (resultList != null && !resultList.isEmpty()) {
                            String previousText = ingredientInput.getText().toString();
                            ingredientInput.setText(previousText + "\n" + resultList.get(0));
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


//
//// Initialize the Vertex AI service and the generative model
//// Specify a model that supports your use case
//// Gemini 1.5 models are versatile and can be used with all API capabilities
//        GenerativeModel gm = FirebaseVertexAI.getInstance()
//                .generativeModel("gemini-1.5-flash-preview-0514");
//
//// Use the GenerativeModelFutures Java compatibility layer which offers
//// support for ListenableFuture and Publisher APIs
//GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//
//// Provide a prompt that contains text
//        Content prompt = new Content.Builder()
//                .addText("Write a story about a magic backpack.")
//                .build();
//
//// To generate text output, call generateContent with the text input
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                String resultText = result.getText();
//                Log.e("alma",resultText);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//            }
//        }, this.getApplicationContext().getMainExecutor());
//

        ingredientInput = findViewById(R.id.ingredientInput);
        voiceInputButton = findViewById(R.id.voiceInputButton);

        // יצירת אובייקט לזיהוי דיבור
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        voiceInputButton.setOnClickListener(v -> startSpeechToText());

    }

    private void startSpeechToText() {
        Log.d("SpeechToText", "התחלתי את זיהוי הדיבור");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "תגיד את רכיבי המתכון");
        speechRecognitionLauncher.launch(intent);
    }
}

