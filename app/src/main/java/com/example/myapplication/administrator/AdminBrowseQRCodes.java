/**
 * Activity for browsing QRCodes
 * Used by the Administrator Only
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.administrator.fragments.DeleteFacilityFragment;
import com.example.myapplication.administrator.fragments.DeleteQRCodeFragment;
import com.example.myapplication.objects.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminBrowseQRCodes extends AppCompatActivity implements DeleteQRCodeFragment.DeleteQRCodeDialogListener {
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Button back;
    private TextView header;
    private ListView qrCodesList;
    private QRCodeArrayAdapter qrCodesAdapter;
    private ArrayList<QRCode> dataList;

    /**
     * method to delete QRCodes
     * @param qrCode the qrcode being deleted
     */
    @Override
    public void DeleteQRCode(QRCode qrCode) {
        StorageReference CodeRef = storageReference.child("qrcodes/" + qrCode.getEventID() + "_qr.jpg");
        CodeRef.delete();
        Map<String,Object> updates = new HashMap<>();
        updates.put("qrCodeUrl", FieldValue.delete());
        db.collection("events").document(qrCode.getEventID())
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        qrCodesAdapter.remove(qrCode);
                        qrCodesAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * onCreate function for displaying QRCode information
     * @param savedInstanceState saved Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("event_posters");

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
                                if (url != null) {
                                    dataList.add(thing);
                                    qrCodesAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("AdminBrowseFacilities", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        qrCodesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                QRCode qrCode = dataList.get(i);
                new DeleteQRCodeFragment(qrCode).show(getSupportFragmentManager(), "Delete Facility");
                return false;
            }
        });
    }
}
