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
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.QRCode;

/**
 * fragment used to delete a QRCode after a long click
 * @author noahv
 */
public class DeleteQRCodeFragment extends DialogFragment {
    private QRCode qrCode;
    private DeleteQRCodeFragment.DeleteQRCodeDialogListener listener;

    /**
     * constructor for the fragment
     * @param qrCode qrcode for delete
     */
    public DeleteQRCodeFragment(QRCode qrCode) {
        this.qrCode = qrCode;
    }

    /**
     * interface implemented by the BrowseQRCodeActivity for deleting QRCodes
     */
    public interface DeleteQRCodeDialogListener {
        void DeleteQRCode(QRCode qrCode);
    }

    /**
     * on fragment attach to the current context
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteQRCodeFragment.DeleteQRCodeDialogListener) {
            listener = (DeleteQRCodeFragment.DeleteQRCodeDialogListener) context;
        } else {
            throw new RuntimeException(context + "  must implement DeleteQRCodeDialogListener");
        }
    }

    /**
     * Method for creating the interactive dialog for the Admin to delete QRcodes
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
                .setTitle("Delete QRCode?")
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.DeleteQRCode(qrCode);
                })
                .setPositiveButton("Cancel", null)
                .create();
    }
}
