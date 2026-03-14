package com.example.neighbourly.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.neighbourly.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.btnCaptureFood.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.foodImageView.setImageBitmap(imageBitmap);

            // Trigger AI Estimation & Upload
            estimateAndUpload(imageBitmap);
        }
    }

    private void estimateAndUpload(Bitmap bitmap) {
        // 1. Placeholder for AI Logic (Gemini API Call)
        int estimatedCalories = 450; // Mock value from AI

        // 2. Upload to Firebase Storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String path = "foods/" + FirebaseAuth.getInstance().getUid() + "/" + System.currentTimeMillis() + ".jpg";
        FirebaseStorage.getInstance().getReference(path).putBytes(data)
                .addOnSuccessListener(task -> {
                    saveMealToFirestore(estimatedCalories);
                    Toast.makeText(getContext(), "Meal Logged!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMealToFirestore(int calories) {
        Map<String, Object> meal = new HashMap<>();
        meal.put("calories", calories);
        meal.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("meals").add(meal);
    }
}