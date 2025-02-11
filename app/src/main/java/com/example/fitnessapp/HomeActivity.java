package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.adapters.PostAdapter;
import com.example.fitnessapp.models.Post;
import com.example.fitnessapp.utils.ApiHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private ImageView ivLogout, ivSearch;
    private Button btnLogNewActivity, btnCreateNewPost;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onResume() {
        super.onResume();
        loadFeed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivLogout = findViewById(R.id.ivLogout);
        ivSearch = findViewById(R.id.ivSearch);
        recyclerView = findViewById(R.id.recyclerView);
        btnLogNewActivity = findViewById(R.id.btnLogNewActivity);
        btnCreateNewPost = findViewById(R.id.btnCreateNewPost);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        ivSearch.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));

        ivLogout.setOnClickListener(view -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });


        btnLogNewActivity.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, NewLogActivity.class)));
        btnCreateNewPost.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, CreatePostActivity.class)));

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(this::navigateToScreen);

        loadFeed();
    }

    private boolean navigateToScreen(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_challenges) {
            startActivity(new Intent(this, ChallengesActivity.class));
            finish();
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, MyProfileActivity.class));
            finish();
        }
        return true;
    }

    private void loadFeed() {
        String url = "http://10.0.2.2:3000/posts/feed";

        // Retrieve token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        postList.clear();
                        JSONArray postsArray = response.getJSONArray("posts");

                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject postObj = postsArray.getJSONObject(i);

                            int postID = postObj.getInt("postID");
                            String firstName = postObj.getString("firstName");
                            String lastName = postObj.getString("lastName");
                            String username = postObj.getString("username");
                            String activityTypeName = postObj.getString("activityTypeName");
                            int duration = postObj.getInt("duration");
                            int caloriesBurned = postObj.getInt("caloriesBurned");
                            String content = postObj.getString("content");
                            String timestamp = postObj.getString("timestamp");
                            int likesCount = postObj.getInt("likeCount");
                            int commentsCount = postObj.getInt("commentCount");

                            postList.add(new Post(postID, firstName, lastName, username, activityTypeName, duration, caloriesBurned,content, timestamp, likesCount, commentsCount));
                        }

                        postAdapter = new PostAdapter(HomeActivity.this, postList);
                        recyclerView.setAdapter(postAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(HomeActivity.this, "Failed to load feed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Add Authorization Header
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

}

