package com.example.myapplication.organizer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImpl;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.util.ArrayList;



/**
 * Activity for displaying a map with user annotations based on event data.
 */
public class MapActivity extends AppCompatActivity {
//    private final String MAP_API_KEY = BuildConfig.MAPTILER_API_KEY;
    private Button backBtn;
    private MapView mapView;
    private String eventId;
    private FirebaseFirestore db;




    /**
     * Called when the activity is first created.
     * Initializes the Firebase Firestore instance, sets up the map, and retrieves event data.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);;
        setContentView(R.layout.activity_map_activity);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        backBtn = findViewById(R.id.mapActivityBackBtn);
        mapView = findViewById(R.id.mapView);

        // Load the map style and add map annotations
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addEntrantsToMap();
            }
        });



        // Set the back button to finish the activity
        backBtn.setOnClickListener(view -> {
            finish();
        });

    }


    /**
     * Retrieves the waiting list of users from the Firestore "events" collection and adds their locations to the map.
     */
    private void addEntrantsToMap() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> waitingList = (ArrayList<String>) documentSnapshot.get("waitlist");

                        if (waitingList != null) {
                            for (String user : waitingList) {
                                db.collection("users").document(user).get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {
                                                String firstname = documentSnapshot1.getString("Firstname");
                                                String lastname = documentSnapshot1.getString("Lastname");
                                                String name = firstname + " " + lastname;
                                                GeoPoint location = documentSnapshot1.getGeoPoint("location");
                                                if (location != null) {
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    addAnnotationToMap(longitude, latitude, name);
                                                } else {
                                                    Log.d("Map Activity getLocation", "Location does not exist");
                                                }
                                            } else {
                                                Log.d("Map Activity getUser", "User does not exist");
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.d("Map Activity getUser", "Error accessing user data");
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error accessing event data. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Adds an annotation (marker) to the map at the specified location.
     *
     * @param longitude the longitude of the marker
     * @param latitude  the latitude of the marker
     * @param name      the name associated with the marker
     */
    private void addAnnotationToMap(double longitude, double latitude, String name) {
        Bitmap bitmap = bitmapFromDrawableRes(this, R.drawable.red_marker);

        if (bitmap != null && mapView != null) {
            AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);

            PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage(bitmap);

            PointAnnotation pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);

            pointAnnotationManager.addClickListener(annotation -> {
                // Check if the clicked annotation is the one you added
                if (annotation.getId() == (pointAnnotation.getId())) {
                    // Show a toast with the name
                    Toast.makeText(this, "User: " + name, Toast.LENGTH_SHORT).show();
                }
                return true; // Return true to indicate the click event is handled
            });
        }
    }


    /**
     * Converts a drawable resource to a Bitmap object.
     *
     * @param context    the context of the application
     * @param resourceId the resource ID of the drawable
     * @return the bitmap representation of the drawable
     */
    private Bitmap bitmapFromDrawableRes(Context context, @DrawableRes int resourceId) {
        Drawable drawable = AppCompatResources.getDrawable(context, resourceId);
        return convertDrawableToBitmap(drawable);
    }


    /**
     * Converts a Drawable object to a Bitmap object.
     *
     * @param sourceDrawable the drawable to convert
     * @return the bitmap representation of the drawable, or null if the drawable is invalid
     */
    private Bitmap convertDrawableToBitmap(Drawable sourceDrawable) {
        if (sourceDrawable ==  null) {
            return null;
        }

        if (sourceDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) sourceDrawable).getBitmap();
        } else {
            Drawable.ConstantState constantState = sourceDrawable.getConstantState();
            if (constantState == null) {
                return null;
            }

            Drawable drawable = constantState.newDrawable().mutate();
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }


}