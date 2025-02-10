package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.utils.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewLogActivity extends AppCompatActivity {
    private Spinner spinnerActivityType;
    private NumberPicker numberPickerDuration;
    private Button btnSaveActivity, btnPostActivity, btnCancelActivity;
    private ImageView ivBack;
    private Map<String, Integer> activityTypeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log);

        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        numberPickerDuration = findViewById(R.id.numberPickerDuration);
        btnSaveActivity = findViewById(R.id.btnSaveActivity);
        btnPostActivity = findViewById(R.id.btnPostActivity);
        btnCancelActivity = findViewById(R.id.btnCancelActivity);
        ivBack = findViewById(R.id.ivBack);

        populateActivityTypes();
        setupNumberPicker();

        btnSaveActivity.setOnClickListener(view -> showSaveConfirmationDialog());
        btnPostActivity.setOnClickListener(view -> goToCreatePost());
        btnCancelActivity.setOnClickListener(view -> showCancelConfirmationDialog());

        ivBack.setOnClickListener(view -> finish());
    }
    private void populateActivityTypes() {
        activityTypeMap = new HashMap<>();
        activityTypeMap.put("Running", 1);
        activityTypeMap.put("Walking", 2);
        activityTypeMap.put("Cycling", 3);
        activityTypeMap.put("Swimming", 4);
        activityTypeMap.put("Yoga", 5);
        activityTypeMap.put("Strength Training", 6);
        activityTypeMap.put("Jump Rope", 7);
        activityTypeMap.put("Rowing", 8);
        activityTypeMap.put("Hiking", 9);
        activityTypeMap.put("Dancing", 10);
        activityTypeMap.put("HIIT Workout", 11);
        activityTypeMap.put("Boxing", 12);
        activityTypeMap.put("Tennis", 13);
        activityTypeMap.put("Basketball", 14);
        activityTypeMap.put("Soccer", 15);
        activityTypeMap.put("Skiing", 16);
        activityTypeMap.put("Skating", 17);
        activityTypeMap.put("Pilates", 18);
        activityTypeMap.put("Rowing Machine", 19);
        activityTypeMap.put("Climbing", 20);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(activityTypeMap.keySet()));
        spinnerActivityType.setAdapter(adapter);
    }

    private void setupNumberPicker() {
        numberPickerDuration.setMinValue(1);
        numberPickerDuration.setMaxValue(300);
        numberPickerDuration.setValue(10); // Default value set to 10 minutes
        numberPickerDuration.setWrapSelectorWheel(true);
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Activity")
                .setMessage("Are you sure you want to cancel? Your changes will not be saved.")
                .setPositiveButton("Yes", (dialog, which) -> finish()) //  Closes activity
                .setNegativeButton("No", null) // Dismisses dialog
                .show();
    }

    private void showSaveConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Save")
                .setMessage("Do you want to save this activity?")
                .setPositiveButton("Yes", (dialog, which) -> logNewActivity()) // Saves activity
                .setNegativeButton("No", null) // Dismisses dialog
                .show();
    }

    private void goToCreatePost() {
        String selectedActivity = spinnerActivityType.getSelectedItem().toString();
        int activityTypeID = activityTypeMap.get(selectedActivity);
        int duration = numberPickerDuration.getValue();

        Intent intent = new Intent(NewLogActivity.this, CreatePostActivity.class);
        intent.putExtra("activityTypeID", activityTypeID);
        intent.putExtra("activityName", selectedActivity);
        intent.putExtra("duration", duration);
        startActivity(intent);
    }

    private void logNewActivity() {
        // Get Selected Activity Type and Duration
        String selectedActivity = spinnerActivityType.getSelectedItem().toString();
        int activityTypeID = activityTypeMap.get(selectedActivity);
        int duration = numberPickerDuration.getValue();

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Toast.makeText(NewLogActivity.this, "Activity logged successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(NewLogActivity.this, "Failed to log activity", Toast.LENGTH_SHORT).show()
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