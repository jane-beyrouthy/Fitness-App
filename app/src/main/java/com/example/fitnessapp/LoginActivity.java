package com.example.fitnessapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.utils.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;
    private ImageView togglePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (prefs.getString("auth_token", null) != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        togglePassword = findViewById(R.id.togglePassword);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners for password visibility toggles
        if (togglePassword != null) {
            togglePassword.setOnClickListener(view -> togglePasswordVisibility(passwordInput, togglePassword));
        }

        loginButton.setOnClickListener(view -> loginUser());
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar and disable button
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        String url = "http://10.0.2.2:3000/auth/login";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    // Hide progress bar and re-enable button
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    try {
                        String token = response.getString("token");
                        String username = response.getString("username");
                        String firstName = response.getString("firstName");
                        String lastName = response.getString("lastName");

                        // Store token in SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", token);
                        editor.putString("username", username);
                        editor.putString("firstName", firstName);
                        editor.putString("lastName", lastName);
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Hide progress bar and re-enable button
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Login Failed! Check your credentials.", Toast.LENGTH_SHORT).show();
                }
        );

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }
    // Toggle Password Visibility
    private void togglePasswordVisibility(EditText passwordField, ImageView toggleButton) {
        boolean isVisible = passwordField.getTransformationMethod() instanceof HideReturnsTransformationMethod;
        if (isVisible) {
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        } else {
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibilty);
        }
        passwordField.setSelection(passwordField.getText().length()); // Keep cursor at the end
    }
}