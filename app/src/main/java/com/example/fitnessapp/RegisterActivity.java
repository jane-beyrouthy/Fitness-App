package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.R;
import com.example.fitnessapp.utils.ApiHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, firstNameInput, lastNameInput, confirmPasswordInput;
    private ImageView togglePassword, toggleConfirmPassword;
    private Button registerButton, loginRedirectButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameInput = findViewById(R.id.firstName);
        lastNameInput =findViewById(R.id.lastName);
        usernameInput = findViewById(R.id.username);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirmPassword);

        togglePassword = findViewById(R.id.togglePassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);

        registerButton = findViewById(R.id.registerButton);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);

        progressBar = findViewById(R.id.progressBar);

        // Set click listeners for password visibility toggles
        if (togglePassword != null) {
            togglePassword.setOnClickListener(view -> togglePasswordVisibility(passwordInput, togglePassword));
        }

        if (toggleConfirmPassword != null) {
            toggleConfirmPassword.setOnClickListener(view -> togglePasswordVisibility(confirmPasswordInput, toggleConfirmPassword));
        }

        registerButton.setOnClickListener(view -> registerUser());
        loginRedirectButton.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input fields
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // API URL
        String url = "http://10.0.2.2:3000/auth/register";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("firstName", firstName);
            requestBody.put("lastName", lastName);
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // API Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Registration failed! Try again.", Toast.LENGTH_SHORT).show();
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
