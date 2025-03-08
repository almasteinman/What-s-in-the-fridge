package com.example.whatsinthefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Set LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Create default data
        List<Recipe> recipes = new ArrayList<>();
       // recipes.add(new Recipe("Pasta", "Delicious pasta with tomato sauce", R.drawable.pasta));
       // recipes.add(new Recipe("Pizza", "Cheesy and tasty pizza", R.drawable.pizza));
       // recipes.add(new Recipe("Salad", "Healthy green salad", R.drawable.salad));

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        if (intent.hasExtra("recipe_data")) {
            String recipeData = intent.getStringExtra("recipe_data");
            Log.d("RecipeList", "📥 התקבלו נתונים: " + recipeData);

            // פירוק תשובת Gemini והוספת מתכונים לרשימה
            String[] rawrecipeLines = recipeData.split("\\*\\*\\d", 4); // פיצול לפי **1, **2, **3 (תוך הגבלת פיצול ל-3 חלקים בלבד)
            for (int i = 1; i < rawrecipeLines.length; i++) { // מתחילים מ-1 כי החלק הראשון הוא לפני **1

                String[] ingredients = {"alma", "shira"};
                String[] instructions = {"alma1", "shira1"};
                String trimmedText = rawrecipeLines[i].trim(); // מסירים רווחים מיותרים
                recipes.add(new Recipe(getFirstWords(trimmedText, 10), "blabla", R.drawable.pasta, ingredients, instructions));
            }
        }
            //String[] recipeLines = recipeData.split("\n");
//            for (String line : recipeLines) {
//                if (!line.trim().isEmpty()) {
//                    String[] parts = line.split(":", 2);
//                    if (parts.length == 2) {
//                        String name = parts[0].trim();
//                        String description = parts[1].trim();
//                        String[] ingredients = {"alma", "shira"};
//                        String[] instructions = {"alma1", "shira1"};
//                        recipes.add(new Recipe(name, description, R.drawable.pasta,ingredients,instructions));
//                    }
//                }
//            }

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipes);
        // Set Adapter
        RecipeAdapter adapter = new RecipeAdapter(recipes);
        recyclerView.setAdapter(adapter);
    }

    private static String getFirstWords(String text, int wordLimit) {
        String[] words = text.split("\\s+"); // מחלקים לפי רווחים
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(words.length, wordLimit); i++) {
            result.append(words[i]).append(" ");
        }
        return result.toString().trim(); // מחזירים מחרוזת מסודרת
    }
}
