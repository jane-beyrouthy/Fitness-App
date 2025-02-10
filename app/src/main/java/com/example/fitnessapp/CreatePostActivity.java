package com.example.fitnessapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.utils.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etPostContent;
    private Button btnSavePost, btnCancelPost;
    private Spinner spinnerActivityType;
    private NumberPicker numberPickerDuration;
    private ImageView ivBack;
    private Map<String, Integer> activityTypeMap = new HashMap<>();
    private List<String> activityTypeNames = new ArrayList<>();
    private int selectedActivityID;
    private String selectedActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etPostContent = findViewById(R.id.etPostContent);
        btnSavePost = findViewById(R.id.btnSavePost);
        btnCancelPost = findViewById(R.id.btnCancelPost);
        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        numberPickerDuration = findViewById(R.id.numberPickerDuration);
        ivBack = findViewById(R.id.ivBack);

        setupNumberPicker();
        fetchActivityTypes();
        autoFillIfFromLogNewActivity();

        btnSavePost.setOnClickListener(view -> showSaveConfirmationDialog());
        btnCancelPost.setOnClickListener(view -> showCancelConfirmationDialog());

        ivBack.setOnClickListener(view -> showCancelConfirmationDialog());
    }
    private void setupNumberPicker() {
        numberPickerDuration.setMinValue(1);
        numberPickerDuration.setMaxValue(300);
        numberPickerDuration.setValue(10); // Default value set to 10 minutes
        numberPickerDuration.setWrapSelectorWheel(true);
    }

    private void fetchActivityTypes() {
        String url = "http://10.0.2.2:3000/activity-types/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray activityTypesArray = response.getJSONArray("activityTypes");
                        activityTypeNames.clear();
                        activityTypeMap.clear();

                        for (int i = 0; i < activityTypesArray.length(); i++) {
                            JSONObject activityObj = activityTypesArray.getJSONObject(i);
                            int id = activityObj.getInt("activityTypeID");
                            String name = activityObj.getString("name");

                            activityTypeNames.add(name);
                            activityTypeMap.put(name, id);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activityTypeNames);
                        spinnerActivityType.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(CreatePostActivity.this, "Failed to load activity types", Toast.LENGTH_SHORT).show()
        );

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private void autoFillIfFromLogNewActivity() {
        if (getIntent().hasExtra("activityTypeID")) {
            String activityName = getIntent().getStringExtra("activityName");
            int duration = getIntent().getIntExtra("duration", 10);
            spinnerActivityType.setSelection(new ArrayList<>(activityTypeMap.keySet()).indexOf(activityName));
            numberPickerDuration.setValue(duration);
        }
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Post")
                .setMessage("Are you sure you want to cancel? Your changes will not be saved.")
                .setPositiveButton("Yes", (dialog, which) -> finish()) //  Closes activity
                .setNegativeButton("No", null) // Dismisses dialog
                .show();
    }

    private void showSaveConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Post")
                .setMessage("Do you want to save this post?")
                .setPositiveButton("Yes", (dialog, which) -> logNewActivity()) // Saves post
                .setNegativeButton("No", null) // Dismisses dialog
                .show();
    }
    private void logNewActivity() {
        // Get Selected Activity Type and Duration
        String selectedActivity = spinnerActivityType.getSelectedItem().toString();
        int activityTypeID = activityTypeMap.get(selectedActivity);
        int duration = numberPickerDuration.getValue();
        String content = etPostContent.getText().toString().trim();

        if(content.isEmpty()){
            Toast.makeText(this, "Content Post is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve Token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // API URL
        String url = "http://10.0.2.2:3000/activity/log";

        // Create JSON Request Body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("activityTypeID", activityTypeID);
            requestBody.put("duration", duration);
            requestBody.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Toast.makeText(CreatePostActivity.this, "Activity logged successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(CreatePostActivity.this, "Failed to log activity", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); //  Authorization Header
                headers.put("Content-Type", "application/json"); //  JSON Content Type
                return headers;
            }
        };

        // Send Request
        ApiHelper.getInstance(this).addToRequestQueue(request);
    }
}
