package com.example.myapplication.organizer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class ListOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_options);

        String eventId = getIntent().getStringExtra("event_id");

        findViewById(R.id.btn_waiting_list).setOnClickListener(v -> openListPage(eventId, "waiting"));
        findViewById(R.id.btn_invited_list).setOnClickListener(v -> openListPage(eventId, "invited"));
        findViewById(R.id.btn_cancelled_list).setOnClickListener(v -> openListPage(eventId, "cancelled"));
    }

    private void openListPage(String eventId, String listType) {
        Intent intent = new Intent(this, ListViewActivity.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("listType", listType);
        startActivity(intent);
    }
}
