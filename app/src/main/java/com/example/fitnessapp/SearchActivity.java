package com.example.fitnessapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fitnessapp.R;
import com.example.fitnessapp.adapters.FriendAdapter;
import com.example.fitnessapp.models.Friend;
import com.example.fitnessapp.utils.ApiHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FriendAdapter userAdapter;
    private List<Friend> userList = new ArrayList<>();
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize Views
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        ivBack = findViewById(R.id.ivBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new FriendAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);

        // Handle Back Button Click
        ivBack.setOnClickListener(view -> finish());

        // Handle Search Input
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void searchUsers(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        String url = "http://10.0.2.2:3000/users/search?query=" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    userList.clear();
                    try {
                        JSONArray usersArray = response.getJSONArray("users");
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject obj = usersArray.getJSONObject(i);
                            int userID =obj.getInt("userID");
                            String firstName = obj.getString("firstName");
                            String lastName = obj.getString("lastName");
                            String username = obj.getString("username");

                            userList.add(new Friend(userID, firstName, lastName, username));
                        }
                        userAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getAuthToken());
                return headers;
            }
        };

        ApiHelper.getInstance(this).addToRequestQueue(request);
    }

    private String getAuthToken() {
        return getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
    }
}
