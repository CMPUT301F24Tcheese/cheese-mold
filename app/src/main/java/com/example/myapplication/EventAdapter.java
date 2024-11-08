package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.objects.Event;
import java.util.List;

// EventAdapter.java
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener onEventClickListener;

    /**
     * Constructor for initializing the EventAdapter with a list of events
     * and a click listener.
     *
     * @param eventList The list of Event objects to be displayed.
     * @param onEventClickListener The listener for handling event clicks.
     */
    public EventAdapter(List<Event> eventList, OnEventClickListener onEventClickListener) {
        this.eventList = eventList;
        this.onEventClickListener = onEventClickListener;
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder to display an event.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Called by RecyclerView to display data at a specific position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.titleTextView.setText(event.getTitle());
        holder.descriptionTextView.setText(event.getDescription());

        // Load poster image if available
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getPosterUrl())
                    .into(holder.posterImageView);
            holder.posterImageView.setVisibility(View.VISIBLE);
        } else {
            holder.posterImageView.setVisibility(View.GONE);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event); // Trigger click callback with the event
            }
        });
    }

    /**
     * Returns the total number of items that the adapter will display.
     *
     * @return The size of the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder class that holds references to each component of the event item layout
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView posterImageView;

        /**
         * Constructor for initializing the ViewHolder with the item view layout.
         *
         * @param itemView The view representing a single item in the RecyclerView.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitle);
            descriptionTextView = itemView.findViewById(R.id.eventDescription);
            posterImageView = itemView.findViewById(R.id.eventPoster);
        }
    }

    /**
     * Interface to handle event click actions.
     */
    public interface OnEventClickListener {
        void onEventClick(Event event); // Interface to handle event click
    }
}
