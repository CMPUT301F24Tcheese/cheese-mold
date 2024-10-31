/**
 * UserArrayAdapter for displaying events in the browsing functionality for Administrators
 */

package com.example.myapplication.objects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.objects.Users;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<Users> {

    /**
     * contructor for the UserArrayAdapter
     * @param context context
     * @param users ArrayList of user for display
     */
    public UserArrayAdapter(Context context, ArrayList<Users> users) {
        super(context, 0, users);
    }

    /**
     * creates each element in the ListView for displaying the Users and
     * and their respective Roles
     * @param position position
     * @param convertView view
     * @param parent view parent
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_array_adapter, parent, false);
        } else {
            view = convertView;
        }

        Users user = getItem(position);
        TextView Username = view.findViewById(R.id.content_header);
        TextView Role = view.findViewById(R.id.content_subheader);

        assert user != null;
        Username.setText(user.getName());
        Role.setText(user.getRole());
        return view;
    }

}
