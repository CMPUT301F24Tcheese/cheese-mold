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
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.QRCode;

import java.util.ArrayList;

public class QRCodeArrayAdapter extends ArrayAdapter<QRCode> {

    public QRCodeArrayAdapter(Context context, ArrayList<QRCode> QRCodes) {
        super(context, 0, QRCodes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.qrcode_array_adapter, parent, false);
        } else {
            view = convertView;
        }

        QRCode qr = getItem(position);
        ImageView code = view.findViewById(R.id.qrcode_display);
        TextView event = view.findViewById(R.id.qrcode_event);

        assert qr != null;
        Glide.with(QRCodeArrayAdapter.this.getContext()).load(qr.getUrl()).into(code);
        event.setText(qr.getEventName());
        return view;
    }
}