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
import com.example.myapplication.administrator.AdminBrowseUsers;
import com.example.myapplication.objects.QRCode;
import com.example.myapplication.objects.Users;

public class DeleteUserFragment extends DialogFragment {
    private Users user;
    private DeleteUserFragment.DeleteUserDialogListenerView listenerView;

    /**
     * constructor for the fragment
     * @param user user for deleting
     */
    public DeleteUserFragment(Users user) {
        this.user = user;
    }

    /**
     * interface for deleting the user in the activity
     */
    public interface DeleteUserDialogListenerView {
        void setDeleted(Users users);
    }

    /**
     * on fragment attach to the current context
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteUserFragment.DeleteUserDialogListenerView) {
            listenerView = (DeleteUserFragment.DeleteUserDialogListenerView) context;
        } else {
            throw new RuntimeException(context + "  must implement DeleteUserDialogListenerView");
        }
    }

    /**
     * Method for creating the interactive dialog for the Admin to delete Users
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
                .setTitle("Delete User?")
                .setNegativeButton("Delete", (dialog, which) -> {
                    listenerView.setDeleted(user);
                })
                .setPositiveButton("Cancel", null)
                .create();
    }

}
