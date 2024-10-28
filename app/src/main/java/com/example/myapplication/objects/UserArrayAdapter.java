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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<Users> {

    public UserArrayAdapter(Context context, ArrayList<Users> users) {
        super(context, 0, users);
    }

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
