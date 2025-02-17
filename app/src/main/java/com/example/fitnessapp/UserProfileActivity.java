package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.R;
import com.example.fitnessapp.models.Friend;
import com.example.fitnessapp.utils.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private TextView tvFullName, tvUsername, btnFollow;
    private ImageView ivBack;
    private String fullName, username;
    private int userID;
    private List<Friend> friendList = new ArrayList<>();
    private boolean isFollowing; // Track follow status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Views
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);
        ivBack = findViewById(R.id.ivBack);
        btnFollow = findViewById(R.id.btnFollow);

        // Get data from intent
        Intent intent = getIntent();
        userID = intent.getIntExtra("friendID", -1);
        fullName = intent.getStringExtra("fullName");
        username = intent.getStringExtra("username");

        // Set User Info
        tvFullName.setText(fullName);
        tvUsername.setText("@" + username);

        // Load Follow Status
        checkFollowStatus();


        // Back Button
        ivBack.setOnClickListener(view -> finish());

        // Follow/Unfollow Button Click
        btnFollow.setOnClickListener(view -> {
            if (isFollowing) {
                unfollowUser();
            } else {
                followUser();
            }
        });
    }

    private void checkFollowStatus() {
        String url = "http://10.0.2.2:3000/friends/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    friendList.clear();
                    try {
                        JSONArray friendsArray = response.getJSONArray("friends");
                        for (int i = 0; i < friendsArray.length(); i++) {
                            JSONObject obj = friendsArray.getJSONObject(i);
                            int friendID = obj.getInt("friendID");
                            String firstName = obj.getString("firstName");
                            String lastName = obj.getString("lastName");
                            String username = obj.getString("username");

                            if (friendID == userID ){
                                isFollowing = true;
                                break;
                            }
                            friendList.add(new Friend(friendID,firstName, lastName,username));
                        }
                        updateFollowButton();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(UserProfileActivity.this, "Failed to check follow status", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void followUser() {
        String url = "http://10.0.2.2:3000/friends/follow";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("friendUsername", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    isFollowing = true;
                    updateFollowButton();
                    Toast.makeText(UserProfileActivity.this, "You are now following " + fullName, Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(UserProfileActivity.this, "Failed to follow user", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void unfollowUser() {
        String url = "http://10.0.2.2:3000/friends/unfollow";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("friendID", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    isFollowing = false;
                    updateFollowButton();
                    Toast.makeText(UserProfileActivity.this, "You have unfollowed " + fullName, Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(UserProfileActivity.this, "Failed to unfollow user", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void updateFollowButton() {
        if (isFollowing) {
            btnFollow.setText("Unfollow");
        } else {
            btnFollow.setText("Follow");
        }
    }

    private Map<String, String> getAuthHeaders() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
