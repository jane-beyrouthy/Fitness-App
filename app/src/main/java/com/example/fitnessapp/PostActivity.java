package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.R;
import com.example.fitnessapp.adapters.CommentAdapter;
import com.example.fitnessapp.models.Comment;
import com.example.fitnessapp.utils.ApiHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private TextView tvPostOwner, tvActivityInfo, tvPostContent, tvPostLikes, tvPostComments, tvNoComments, tvCaloriesBurned;
    private ImageView ivLike, ivComment, ivBack;
    private EditText etComment;
    private Button btnPostComment;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private int postID;
    private boolean isLiked = false;
    private int likesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Initialize Views
        tvPostOwner = findViewById(R.id.tvPostOwner);
        tvActivityInfo = findViewById(R.id.tvActivityInfo);
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvPostLikes = findViewById(R.id.tvPostLikes);
        tvPostComments = findViewById(R.id.tvPostComments);
        ivLike = findViewById(R.id.ivLike);
        ivComment = findViewById(R.id.ivComment);
        ivBack = findViewById(R.id.ivBack);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);
        rvComments = findViewById(R.id.rvComments);
        tvNoComments = findViewById(R.id.tvNoComments);

        // RecyclerView Setup
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, commentList);
        rvComments.setAdapter(commentAdapter);

        // Retrieve postID from Intent
        postID = getIntent().getIntExtra("postID",-1);
        if (postID == -1) {
            Toast.makeText(this, "Invalid Post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set Click Listeners
        ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(PostActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Ensures `PostActivity` is removed from the back stack
        });

        ivLike.setOnClickListener(view -> toggleLikePost());
        btnPostComment.setOnClickListener(view -> postComment());

        // Load Post Details & Comments
        loadPostDetails();
        loadComments();
    }

    private void loadPostDetails() {
        String url = "http://10.0.2.2:3000/posts/" + postID + "/details";

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
                        // Debugging: Print full response
                        System.out.println("Post Details Response: " + response.toString());

                        // Extract the "post" object from the response
                        if (!response.has("post")) {
                            Toast.makeText(PostActivity.this, "Invalid response format", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject postObj = response.getJSONObject("post");

                        // Ensure required keys exist
                        if (!postObj.has("firstName") || !postObj.has("activityTypeName")) {
                            Toast.makeText(PostActivity.this, "Incomplete post data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Extract data from JSON response
                        String firstName = postObj.getString("firstName");
                        String lastName = postObj.getString("lastName");
                        String username = postObj.getString("username");
                        String activityTypeName = postObj.getString("activityTypeName");
                        int duration = postObj.getInt("duration");
                        int caloriesBurned = postObj.getInt("caloriesBurned");
                        String content = postObj.getString("content");
                        likesCount = postObj.getInt("likeCount");
                        int commentsCount = postObj.getInt("commentCount");

                        // Set UI Data
                        tvPostOwner.setText(firstName + " " + lastName + " - @" + username);
                        tvActivityInfo.setText(activityTypeName + " - " + duration + " mins");
                        tvCaloriesBurned.setText("Calories Burned: "+ caloriesBurned +" kcal");
                        tvPostContent.setText(content);
                        tvPostLikes.setText(likesCount + " Likes");
                        tvPostComments.setText(commentsCount + " Comments");

                        updateLikeUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PostActivity.this, "Error parsing post details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Failed to load post details";
                    if (error.networkResponse != null) {
                        errorMessage += ": HTTP " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(PostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    System.out.println("Post Details Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }



    private void updateLikeUI() {
        tvPostLikes.setText(likesCount + " Likes");
        ivLike.setColorFilter(isLiked ? Color.BLUE : Color.GRAY);
    }

    private void toggleLikePost() {
        String url = "http://10.0.2.2:3000/posts/" + postID + "/like";
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    isLiked = !isLiked;
                    likesCount += isLiked ? 1 : -1;
                    updateLikeUI();
                },
                error -> Toast.makeText(PostActivity.this, "Failed to like post", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void loadComments() {
        String url = "http://10.0.2.2:3000/posts/" + postID + "/comments-list";

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Debugging: Print full response
                        System.out.println("Comments Response: " + response.toString());

                        // Extract "comments" array from the response
                        if (!response.has("comments")) {
                            Toast.makeText(PostActivity.this, "Invalid response format", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray commentsArray = response.getJSONArray("comments");

                        commentList.clear();
                        if (commentsArray.length() == 0) {
                            tvNoComments.setVisibility(View.VISIBLE);  //  Show "No comments yet"
                        } else {
                            tvNoComments.setVisibility(View.GONE);  //  Hide if comments exist
                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject commentObj = commentsArray.getJSONObject(i);

                                // Ensure required keys exist
                                if (!commentObj.has("username") || !commentObj.has("content")) {
                                    Toast.makeText(PostActivity.this, "Invalid comment format", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String username = commentObj.getString("username");
                                String content = commentObj.getString("content");

                                commentList.add(new Comment(username, content));
                            }
                            commentAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PostActivity.this, "Error parsing comments", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Failed to load comments";
                    if (error.networkResponse != null) {
                        errorMessage += ": HTTP " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(PostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    System.out.println("Comments Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }



    private void postComment() {
        String commentContent = etComment.getText().toString().trim();

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog before posting the comment
        new AlertDialog.Builder(this)
                .setTitle("Confirm Comment")
                .setMessage("Do you want to post this comment?")
                .setPositiveButton("Yes", (dialog, which) -> sendCommentToServer(commentContent))
                .setNegativeButton("No", null)
                .show();
    }

    private void sendCommentToServer(String commentContent) {
        String url = "http://10.0.2.2:3000/posts/" + postID + "/comment";
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("content", commentContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    etComment.setText("");
                    Toast.makeText(PostActivity.this, "Comment posted!", Toast.LENGTH_SHORT).show();
                    reloadActivity(); //  Reload activity to update comments count and list
                },
                error -> Toast.makeText(PostActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
