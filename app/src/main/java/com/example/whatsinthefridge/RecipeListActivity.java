package com.example.whatsinthefridge;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsinthefridge.Recipe;
import com.google.android.material.carousel.CarouselLayoutManager;

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


        // Create data
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Pasta", "Delicious pasta with tomato sauce",R.drawable.pasta ));
        recipes.add(new Recipe("Pizza", "Cheesy and tasty pizza", R.drawable.pizza));
        recipes.add(new Recipe("Salad", "Healthy green salad", R.drawable.salad));

    // Set Adapter
    RecipeAdapter adapter = new RecipeAdapter(recipes);
    recyclerView.setAdapter(adapter);
}
}

