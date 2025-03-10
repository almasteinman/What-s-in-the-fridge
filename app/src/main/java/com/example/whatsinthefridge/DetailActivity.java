package com.example.whatsinthefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.Arrays;

public class DetailActivity extends AppCompatActivity {

    TextView  recipeName, ingredientsText, instructionsText;
    ImageView detailImage;
    Button backButton ;
    String key = "";
//    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detailImage = findViewById(R.id.detailImage);
        recipeName = findViewById(R.id.recipeName);
        ingredientsText = findViewById(R.id.ingredientsText);
        instructionsText = findViewById(R.id.instructionsText);
        backButton = findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            recipeName.setText(bundle.getString("Title"));

            //imageUrl = bundle.getString("Image");
            key = bundle.getString("Key");
            //Glide.with(this).load(bundle.getString("Image")).into(detailImage);

            // קבלת רשימות המרכיבים וההוראות
            String[] ingredients = bundle.getStringArray("Ingredients");
            Log.d("ALMA", "📌 Ingredients received: " + Arrays.toString(ingredients));
            String[] instructions = bundle.getStringArray("Instructions");
            Log.d("ALMA", "📌 Instructions received: " + Arrays.toString(instructions));
            
            // הפיכת מערכים לטקסטים מופרדים בשורות
            if (ingredients != null) {
                ingredientsText.setText("Ingredients:\n" + String.join("\n", ingredients));
            }

            if (instructions != null) {
                instructionsText.setText("Instructions:\n" + String.join("\n", instructions));
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, IngredientsActivity.class);
                startActivity(intent);
            }
        });
    }
}