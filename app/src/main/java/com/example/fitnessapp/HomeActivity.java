package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.adapters.PostAdapter;
import com.example.fitnessapp.models.Post;
import com.example.fitnessapp.utils.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TextView tvUsername;
    private ImageView ivLogout, ivNotifications;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUsername = findViewById(R.id.tvUsername);
        ivLogout = findViewById(R.id.ivLogout);
        ivNotifications = findViewById(R.id.ivNotifications);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
        loadFeed();

        /*tvUsername.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });*/

        ivLogout.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        /*ivNotifications.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
        });*/
    }


    private void loadFeed() {
        // Fetch posts from backend and update RecyclerView...
    }

    private void loadNotifications() {
        // Fetch unread notifications...
    }
}

