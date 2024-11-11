package com.example.myapplication.administrator.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.myapplication.objects.Facility;
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
import com.example.myapplication.objects.Facility;

/**
 * fragment used to delete a facility after a long click
 * @author noahv
 */
public class DeleteFacilityFragment extends DialogFragment {
    private Facility facility;
    private DeleteFacilityDialogueListener listener;

    /**
     * constructor for the fragment
     * @param facility the facility being deleted
     */
    public DeleteFacilityFragment(Facility facility) {
        this.facility = facility;
    }

    /**
     * interface implemented by the BrowseFacilityActivity for deleting facilities
     */
    public interface DeleteFacilityDialogueListener {
        void DeleteFacility(Facility facility);
    }

    /**
     * on fragment attach to the current context
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteFacilityDialogueListener) {
            listener = (DeleteFacilityDialogueListener) context;
        } else {
            throw new RuntimeException(context + "  must implement AddBookDialogListener");
        }
    }

    /**
     * Method for creating the interactive dialog for the Admin to delete facilities
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
                .setTitle("Delete Facility?")
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.DeleteFacility(facility);
                })
                .setPositiveButton("Cancel", null)
                .create();
    }
}
