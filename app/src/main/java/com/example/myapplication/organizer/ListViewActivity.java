package com.example.myapplication.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Users;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ListViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<Users> dataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView titleTextView;
    private TextView footerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        titleTextView = findViewById(R.id.titleTextView);
        footerTextView = findViewById(R.id.footerTextView);
        recyclerView = findViewById(R.id.recyclerViewLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button backButton = findViewById(R.id.backButton);
        Button removeButton = findViewById(R.id.btn_remove);

        backButton.setOnClickListener(v -> finish());

        String listType = getIntent().getStringExtra("listType");
        String eventId = getIntent().getStringExtra("event_id");

        setTitleForList(listType);

        boolean isInvitedList = "invited".equals(listType);

        listAdapter = new ListAdapter(dataList, isInvitedList);
        recyclerView.setAdapter(listAdapter);

        if (isInvitedList) {
            removeButton.setVisibility(View.VISIBLE);
            removeButton.setOnClickListener(v -> showRemoveConfirmationDialog(eventId));
        } else {
            removeButton.setVisibility(View.GONE);
        }

        if (listType != null && eventId != null) {
            loadListData(eventId, listType);
        } else {
            Toast.makeText(this, "Invalid data passed", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeSelectedUsers(String eventId) {
        List<String> selectedUserIds = listAdapter.getSelectedUserIds();

        if (selectedUserIds.isEmpty()) {
            Toast.makeText(this, "No users selected", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> lotteryList = (List<String>) documentSnapshot.get("lotteryList");
                        if (lotteryList != null) {
                            lotteryList.removeAll(selectedUserIds);

                            db.collection("events").document(eventId)
                                    .update("lotteryList", lotteryList)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Users removed successfully", Toast.LENGTH_SHORT).show();
                                        loadListData(eventId, "invited"); // Refresh the list
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load event data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showRemoveConfirmationDialog(String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Removal")
                .setMessage("Confirm to remove the selected entrants from the list?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Call the removeSelectedUsers method
                    removeSelectedUsers(eventId);
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    // Just dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }



    private void setTitleForList(String listType) {
        String title = "List";
        if ("waiting".equals(listType)) {
            title = "Waiting List";
        } else if ("invited".equals(listType)) {
            title = "Invited List";
        } else if ("cancelled".equals(listType)) {
            title = "Cancelled List";
        } else if ("confirmed".equals(listType)) {
            title = "Confirmed List";
        }
        titleTextView.setText(title);
    }

    private void loadListData(String eventId, String listType) {
        String fieldName = mapListTypeToFieldName(listType);

        if (fieldName == null) {
            Toast.makeText(this, "Invalid list type", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> userIds = (List<String>) documentSnapshot.get(fieldName);
                        if (userIds != null && !userIds.isEmpty()) {
                            dataList.clear();
                            loadUserDetails(userIds);
                        } else {
                            dataList.clear();
                            listAdapter.notifyDataSetChanged();
                            footerTextView.setText("Total: 0 people");
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserDetails(List<String> userIds) {
        dataList.clear();

        for (String userId : userIds) {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("Firstname");
                            String lastName = documentSnapshot.getString("Lastname");
                            String email = documentSnapshot.getString("Email");
                            String profilePicture = documentSnapshot.getString("Profile Picture");
                            String role = documentSnapshot.getString("role");

                            Users user = new Users(userId, firstName, lastName, email, profilePicture, role);
                            dataList.add(user);

                            if (dataList.size() == userIds.size()) {
                                listAdapter.notifyDataSetChanged();
                                footerTextView.setText("Total: " + dataList.size() + " people");
                            }
                        } else {
                            System.out.println("Document not found for UserId: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Failed to load user data for UserId: " + userId);
                        Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private String mapListTypeToFieldName(String listType) {
        if (listType == null) return null;

        switch (listType) {
            case "waiting":
                return "waitlist";
            case "cancelled":
                return "cancelledList";
            case "invited":
                return "lotteryList";
            case "confirmed":
                return "confirmedList";
            default:
                System.out.println("Invalid listType: " + listType);
                return null;
        }
    }
}
