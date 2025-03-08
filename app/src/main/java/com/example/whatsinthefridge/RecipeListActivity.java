package com.example.whatsinthefridge;

import static java.security.AccessController.getContext;

import android.content.Context;
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

import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    RecipeAdapter adapter;
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
                String[] ingredients = getIngridientsFromLines(rawrecipeLines[i]);
                        // {"alma", "shira"};
                String[] instructions = getInstructionsFromLines(rawrecipeLines[i]);
                        //{"alma1", "shira1"};
                String trimmedText = rawrecipeLines[i].trim(); // מסירים רווחים מיותרים
                String name = getFirstWords(trimmedText, 10);

                recipes.add(new Recipe(name, R.drawable.pasta, ingredients, instructions));
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

        adapter = new RecipeAdapter(this,  0,0, recipes);
        recyclerView.setAdapter(adapter);
    }

    private static String getFirstWords(String text, int wordLimit) {
        String[] words = text.split("\\s+"); // מחלקים לפי רווחים
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(words.length, wordLimit); i++) {
            result.append(words[i]).append(" ");
        }
        Log.d("ALMA","getFirstWords"+result.toString().trim());

        return result.toString().trim(); // מחזירים מחרוזת מסודרת
    }

    public static String[] getIngridientsFromLines(String text) {
        ArrayList<String> ingredientsList = new ArrayList<>();

        // Check if the text is empty or null
        if (text == null || text.trim().isEmpty()) {
            return new String[0]; // Return an empty array if no ingredients found
        }

        // Find the section that contains ingredients between "**Ingredients" and "**Instructions"
        int ingredientsStart = text.indexOf("**Ingredients");
        int instructionsStart = text.indexOf("**Instructions");

        // If both sections are found
        if (ingredientsStart != -1 && instructionsStart != -1) {
            String ingredientsSection = text.substring(ingredientsStart + 12, instructionsStart).trim(); // Extract the ingredients part

            // Split the ingredients section by lines
            String[] lines = ingredientsSection.split("\n");

            // Add each line as an ingredient, skipping empty lines
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    ingredientsList.add(trimmedLine);
                }
            }
        }
        Log.d("ALMA","getIngridientsFromLines"+ingredientsList.toArray(new String[0]).toString());
        String[] aaa = ingredientsList.toArray(new String[0]);
        for (int i = 0; i < aaa.length; i++) {
            Log.d("ALMA", " " + aaa[i]);
        }
        // Return the instructions as an array of strings
        return aaa;   //Return the ingredients as an array of strings
    }


    public static String[] getInstructionsFromLines(String text) {
        ArrayList<String> instructionsList = new ArrayList<>();

        // Check if the text is empty or null
        if (text == null || text.trim().isEmpty()) {
            return new String[0]; // Return an empty array if no instructions found
        }

        // Find the section that contains instructions after "**Instructions"
        int instructionsStart = text.indexOf("**Instructions");

        // If the instructions section is found
        if (instructionsStart != -1) {
            String instructionsSection = text.substring(instructionsStart + 14).trim(); // Extract the instructions part

            // Split the instructions section by lines
            String[] lines = instructionsSection.split("\n");

            // Add each line as an instruction, skipping empty lines
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    instructionsList.add(trimmedLine);
                }
            }
        }
        Log.d("ALMA","getInstructionsFromLines"+instructionsList.toArray(new String[0]).toString());
        String[] aaa = instructionsList.toArray(new String[0]);
        for (int i = 0; i < aaa.length; i++) {
            Log.d("ALMA", " " + aaa[i]);
        }
        // Return the instructions as an array of strings
        return aaa;   // instructionsList.toArray(new String[0]);
    }

}
