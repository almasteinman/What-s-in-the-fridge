package com.example.whatsinthefridge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowUsersActivity extends AppCompatActivity implements ValueEventListener {
    ArrayList<String> uList;
    ListView lvUsers;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    Button btnUback;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //add objects to array list
        uList = new ArrayList<String>();

        lvUsers = findViewById(R.id.lvUsers);
        btnUback = findViewById(R.id.btnUback);
        btnUback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, uList);
        lvUsers.setAdapter(adapter);

        //firebase connection
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        eventListener = databaseReference.addValueEventListener(this);

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        uList.clear();
        for (DataSnapshot itemSnapshot: snapshot.getChildren())
        {
            User u = itemSnapshot.getValue(User.class);
            Log.d("ALMA","read DB " + u.getName());
            uList.add(u.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}