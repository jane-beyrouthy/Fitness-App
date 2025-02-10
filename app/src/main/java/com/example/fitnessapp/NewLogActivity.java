package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class NewLogActivity extends AppCompatActivity {
    private Spinner spinnerActivityType;
    private NumberPicker numberPickerDuration;
    private Button btnSaveActivity, btnPostActivity, btnCancelActivity;
    private ImageView ivBack;
    private Map<String, Integer> activityTypeMap = new HashMap<>();
    private List<String> activityTypeNames = new ArrayList<>();
    private String selectedActivity;
    private int selectedActivityID;

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

        setupNumberPicker();
        fetchActivityTypes();

        btnSaveActivity.setOnClickListener(view -> showSaveConfirmationDialog());
        btnPostActivity.setOnClickListener(view -> goToCreatePost());
        btnCancelActivity.setOnClickListener(view -> showCancelConfirmationDialog());

        spinnerActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < activityTypeNames.size()) {
                    selectedActivity = activityTypeNames.get(position); //  Correctly retrieves activity name
                    selectedActivityID = activityTypeMap.getOrDefault(selectedActivity, -1); //  Maps to ID
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedActivity = null;
                selectedActivityID = -1;
            }
        });

        ivBack.setOnClickListener(view -> finish());
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
                error -> Toast.makeText(NewLogActivity.this, "Failed to load activity types", Toast.LENGTH_SHORT).show()
        );

        ApiHelper.getInstance(this).addToRequestQueue(request);
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
        // Ensure user has selected an activity type
        if (spinnerActivityType.getSelectedItem() == null) {
            Toast.makeText(this, "Please select an activity type", Toast.LENGTH_SHORT).show();
            return;
        }

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