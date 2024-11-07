/**
 * ImageArrayAdapter for displaying images in the browsing functionality for Administrators
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.QRCode;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Image> {

    /**
     * constructor for the ImageArrayAdapter
     * @param context context
     * @param images array of image objects for display
     */
    public ImageArrayAdapter(Context context, ArrayList<Image> images) {
        super(context, 0, images);
    }

    /**
     * creates each element in the ListView for displaying the images and
     * and their respective type
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.qrcode_array_adapter, parent, false);
        } else {
            view = convertView;
        }

        Image image = getItem(position);
        ImageView picture = view.findViewById(R.id.qrcode_display);
        TextView type = view.findViewById(R.id.qrcode_event);

        assert image != null;
        Glide.with(ImageArrayAdapter.this.getContext()).load(image.getUrl()).into(picture);
        type.setText(image.getType());
        return view;
    }

}
