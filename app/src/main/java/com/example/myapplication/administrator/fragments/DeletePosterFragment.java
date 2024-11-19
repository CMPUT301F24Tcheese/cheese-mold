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
import com.example.myapplication.administrator.Image;

public class DeletePosterFragment extends DialogFragment {
    private Image image;
    private DeletePosterDialogListener listener;

    /**
     * constructor for the fragment
     * @param image image to be deleted
     */
    public DeletePosterFragment(Image image) {
        this.image = image;
    }

    /**
     * interface implemented by the BrowseImagesActivity for deleting poster Pictures
     */
    public interface DeletePosterDialogListener {
        void DeletePoster(Image image);
    }

    /**
     * on fragment attach to the current context
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeletePosterFragment.DeletePosterDialogListener) {
            listener = (DeletePosterFragment.DeletePosterDialogListener) context;
        } else {
            throw new RuntimeException(context + "  must implement DeletePosterDialogListener");
        }
    }

    /**
     * Method for creating the interactive dialog for the Admin to delete posters
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
                .setTitle("Delete Poster?")
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.DeletePoster(image);
                })
                .setPositiveButton("Cancel", null)
                .create();
    }

}
