package com.example.myapplication.entrant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;

public class GeoAlertDialogFragment extends DialogFragment {

    private GeolocationDialogListener listener;
    private Event event;
    private String userId;

    public interface GeolocationDialogListener {
        void onJoinClicked(Event event, String userId);

    }

    public static GeoAlertDialogFragment newInstance(Event event, String userId) {
        GeoAlertDialogFragment fragment = new GeoAlertDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event); // Using Serializable
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GeolocationDialogListener) {
            listener = (GeolocationDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GeolocationDialogListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_geo_alert, container, false);

        // Get event and userId from arguments
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event"); // Cast Serializable to Event
            userId = getArguments().getString("userId");
        }

        if (event == null) {
            Log.e("GeoAlertDialogFragment", "Event is null in GeoAlertDialogFragment");
        }

        Button joinButton = view.findViewById(R.id.geo_join);
        Button cancelButton = view.findViewById(R.id.geo_cancel);

        joinButton.setOnClickListener(v -> {
            if (listener != null && event != null) {
                listener.onJoinClicked(event, userId); // Trigger the onJoinClicked listener
            } else {
                Log.e("GeoAlertDialogFragment", "Listener or Event is null in onJoinClicked");
            }
            dismiss(); // Close the dialog
        });

        cancelButton.setOnClickListener(v -> {

            dismiss(); // Close the dialog
        });

        return view;
    }

}
