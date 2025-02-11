package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.adapters.ActivityAdapter;
import com.example.fitnessapp.adapters.FriendAdapter;
import com.example.fitnessapp.models.Activity;
import com.example.fitnessapp.models.Friend;
import com.example.fitnessapp.utils.ApiHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MyProfileActivity extends AppCompatActivity {
    private TextView tvFullName, tvUsername, tvTitle,btnActivitySummary, btnFriendsList;
    private ImageView ivNotifications, ivLogout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ActivityAdapter activityAdapter;
    private FriendAdapter friendAdapter;
    private List<Activity> activityList = new ArrayList<>();
    private List<Friend> friendList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private boolean isActivitySelected = false; // Track selected list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);
        tvTitle = findViewById(R.id.tvTitle);
        btnActivitySummary = findViewById(R.id.btnActivitySummary);
        btnFriendsList = findViewById(R.id.btnFriendsList);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivLogout = findViewById(R.id.ivLogout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(this::navigateToScreen);

        // Load User Info
        loadUserInfo();

        // Reset UI on Activity Start
        resetSelection();

        btnActivitySummary.setOnClickListener(view -> loadActivitySummary());
        btnFriendsList.setOnClickListener(view -> loadFriendsList());
        //ivNotifications.setOnClickListener(view -> startActivity(new Intent(this, NotificationsActivity.class)));
        ivLogout.setOnClickListener(view -> logoutUser());

    }
    private boolean navigateToScreen(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_challenges) {
            startActivity(new Intent(this, ChallengesActivity.class));
            finish();
        } else if (itemId == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        return true;
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "First");
        String lastName = prefs.getString("lastName", "Last");
        String username = prefs.getString("username", "username");

        tvFullName.setText(firstName + " "+ lastName);
        tvUsername.setText("@" + username);
    }

    private void resetSelection() {
        btnActivitySummary.setBackgroundColor(Color.parseColor("#757575")); //gray
        btnFriendsList.setBackgroundColor(Color.parseColor("#757575")); //gray
        recyclerView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
    }

    private void loadActivitySummary() {
        isActivitySelected = true;
        switchButtonColors();
        tvTitle.setText("My Activities");
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2:3000/track/activities";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    activityList.clear();
                    try {
                        JSONArray activitiesArray = response.getJSONArray("activities");
                        for (int i = 0; i < activitiesArray.length(); i++) {
                            JSONObject obj = activitiesArray.getJSONObject(i);
                            String type = obj.getString("activityTypeName");
                            int sessions = obj.getInt("totalSessions");
                            int duration = obj.getInt("totalDuration");
                            int calories = obj.getInt("totalCalories");

                            activityList.add(new Activity(type, sessions, duration, calories));
                        }
                        activityAdapter = new ActivityAdapter(MyProfileActivity.this, activityList);
                        recyclerView.setAdapter(activityAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MyProfileActivity.this, "Failed to load activities", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String token = prefs.getString("auth_token", null);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void loadFriendsList() {
        isActivitySelected = false;
        switchButtonColors();
        tvTitle.setText("My Friends");
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2:3000/friends/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    friendList.clear();
                    try {
                        JSONArray friendsArray = response.getJSONArray("friends");
                        for (int i = 0; i < friendsArray.length(); i++) {
                            JSONObject obj = friendsArray.getJSONObject(i);
                            String firstName = obj.getString("firstName");
                            String lastName = obj.getString("lastName");
                            String username = obj.getString("username");

                            friendList.add(new Friend(firstName, lastName,username));
                        }
                        friendAdapter = new FriendAdapter(MyProfileActivity.this, friendList);
                        recyclerView.setAdapter(friendAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MyProfileActivity.this, "Failed to load friends", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String token = prefs.getString("auth_token", null);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void switchButtonColors() {
        if (isActivitySelected) {
            btnActivitySummary.setBackgroundColor(Color.parseColor("#2196F3")); //blue
            btnFriendsList.setBackgroundColor(Color.parseColor("#757575")); //gray
        } else {
            btnActivitySummary.setBackgroundColor(Color.parseColor("#757575")); //gray
            btnFriendsList.setBackgroundColor(Color.parseColor("#2196F3")); //blue
        }
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
