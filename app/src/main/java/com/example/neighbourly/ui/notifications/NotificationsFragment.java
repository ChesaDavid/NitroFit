package com.example.neighbourly.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.neighbourly.databinding.FragmentNotificationsBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        loadLeaderboard();

        binding.btnCreateRoom.setOnClickListener(v -> {
            // Logic to open a Dialog and create a Firestore Room
        });

        return binding.getRoot();
    }

    private void loadLeaderboard() {
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("currentSteps", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            sb.append(doc.getString("displayName"))
                                    .append(": ")
                                    .append(doc.getLong("currentSteps"))
                                    .append(" steps\n");
                        }
                        binding.textLeaderboard.setText(sb.toString());
                    }
                });
    }
}