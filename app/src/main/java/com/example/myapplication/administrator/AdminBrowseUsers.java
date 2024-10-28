package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.UserArrayAdapter;
import com.example.myapplication.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseUsers extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView usersList;
    private UserArrayAdapter usersAdapter;
    private ArrayList<Users> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("USER PROFILES");

        dataList = new ArrayList<Users>();

        usersList = findViewById(R.id.contentListView);
        usersAdapter = new UserArrayAdapter(this, dataList);
        usersList.setAdapter(usersAdapter);

        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firstname = document.getString("Firstname");
                                String lastname = document.getString("Lastname");
                                String email = document.getString("Email");
                                String profilePicture = document.getString("Profile Picture");
                                String role = document.getString("role");
                                Users thing = new Users(firstname, lastname, email, profilePicture, role);
                                dataList.add(thing);
                                usersAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

    }
}
