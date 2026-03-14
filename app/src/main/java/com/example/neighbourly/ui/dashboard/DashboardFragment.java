package com.example.neighbourly.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.neighbourly.LoginActivity;
import com.example.neighbourly.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class DashboardFragment extends Fragment implements SensorEventListener {

    private FragmentDashboardBinding binding;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int currentSteps = 0;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase User Profile Info
        loadUserProfile();

        // Initialize Sensors
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        loadDailyNutrition();

        // Setup Logout
        binding.btnLogout.setOnClickListener(v -> handleLogout());

        return binding.getRoot();
    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.profileName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            binding.profileEmail.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(binding.profileImage);
            }
        }
    }

    private void loadDailyNutrition() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        String today = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());

        db.collection("users").document(userId).collection("daily_stats").document(today)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        long calories = value.getLong("totalCalories") != null ? value.getLong("totalCalories") : 0;
                        // Use correct ID from layout
                        // binding.textNutritionValue.setText(calories + " kcal");
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            currentSteps = (int) event.values[0];
            // If you don't have a step counter TextView yet, comment this out or add it to XML
            // binding.textStepCount.setText(String.valueOf(currentSteps));
            updateStepsInFirebase(currentSteps);
        }
    }

    // FIX 1: Add this method to resolve "must be declared abstract"
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for step counter
    }

    private void updateStepsInFirebase(int steps) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            db.collection("users").document(userId).update("currentSteps", steps);
        }
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepSensor != null) sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}