package com.example.myapplication.administrator.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.Users;

public class DeleteEventFragment extends DialogFragment {
    private Event event;
    private DeleteEventFragment.DeleteEventDialogListener listener;

    /**
     * constructor for the fragment
     * @param event event for deleting
     */
    public DeleteEventFragment(Event event) {
        this.event = event;
    }

    /**
     * interface for deleting the event in the activity
     */
    public interface DeleteEventDialogListener {
        void deleteEvent(Event event);
    }

    /**
     * on fragment attach to the current context
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteEventFragment.DeleteEventDialogListener) {
            listener = (DeleteEventFragment.DeleteEventDialogListener) context;
        } else {
            throw new RuntimeException(context + "  must implement DeleteEventDialogListener");
        }
    }

    /**
     * Method for creating the interactive dialog for the Admin to delete Events
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return builder to display the fragment on screen
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.admin_delete_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Delete Event?")
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.deleteEvent(event);
                })
                .setPositiveButton("Cancel", null)
                .create();
    }


}
