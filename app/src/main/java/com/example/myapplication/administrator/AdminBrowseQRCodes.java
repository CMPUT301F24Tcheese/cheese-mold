/**
 * Activity for browsing QRCodes
 * Used by the Administrator Only
 */

package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.QRCodeArrayAdapter;
import com.example.myapplication.objects.QRCode;
import com.example.myapplication.objects.UserArrayAdapter;
import com.example.myapplication.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminBrowseQRCodes extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView qrCodesList;
    private QRCodeArrayAdapter qrCodesAdapter;
    private ArrayList<QRCode> dataList;

    /**
     * onCreate function for displaying QRCode information
     * @param savedInstanceState saved Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("QR Codes");

        dataList = new ArrayList<QRCode>();

        qrCodesList = findViewById(R.id.contentListView);
        qrCodesAdapter = new QRCodeArrayAdapter(this, dataList);
        qrCodesList.setAdapter(qrCodesAdapter);

        // searching firebase events for all existing QRCodes
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("qrCodeUrl");
                                String name = document.getString("name");
                                String id = document.getId();
                                QRCode thing = new QRCode(url, id, name);
                                dataList.add(thing);
                                qrCodesAdapter.notifyDataSetChanged();
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
