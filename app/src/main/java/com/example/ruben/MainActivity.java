package com.example.ruben;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.inapp.CTLocalInApp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etFullName, etPhone, etEmail;
    private Button btnSubmit;
    CleverTapAPI clevertapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"TAM-RO","TAM-RO","YourChannelDescription", NotificationManager.IMPORTANCE_MAX,true);

        // Initialize the EditText fields
        etUsername = findViewById(R.id.et_username);
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);

        // Initialize the Button
        btnSubmit = findViewById(R.id.btn_submit);

        // Set OnClickListener for the Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch values from EditText fields
                String username = etUsername.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                // Check if any fields are empty
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName) ||
                        TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // each of the below mentioned fields are optional
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Name", fullName);    // String
                    profileUpdate.put("Identity", username);      // String or number
                    profileUpdate.put("Email", email); // Email address of the user
                    profileUpdate.put("Phone", "+91" + phone);   // Phone (with the country code, starting with +)
                    profileUpdate.put("MSG-email", true);        // Disable email notifications
                    profileUpdate.put("MSG-push", true);          // Enable push notifications
                    clevertapDefaultInstance.onUserLogin(profileUpdate);

                    Toast.makeText(MainActivity.this, "User successfully logged in", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!clevertapDefaultInstance.isPushPermissionGranted()){
            JSONObject jsonObject = CTLocalInApp.builder()
                    .setInAppType(CTLocalInApp.InAppType.ALERT)
                    .setTitleText("Get Notified")
                    .setMessageText("Enable Notification permission")
                    .followDeviceOrientation(true)
                    .setPositiveBtnText("Allow")
                    .setNegativeBtnText("Cancel")
                    .build();
            clevertapDefaultInstance.promptPushPrimer(jsonObject);
        }
    }
}