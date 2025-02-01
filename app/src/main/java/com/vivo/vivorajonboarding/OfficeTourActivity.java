package com.vivo.vivorajonboarding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIImage;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.hotspots.ActionPLHotspot;
import com.panoramagl.hotspots.HotSpotListener;
import com.panoramagl.structs.PLRange;
import com.panoramagl.utils.PLUtils;
import com.vivo.vivorajonboarding.adapter.LocationsAdapter;
import com.vivo.vivorajonboarding.data.HotspotData;
import com.vivo.vivorajonboarding.data.LocationData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class OfficeTourActivity extends AppCompatActivity implements HotSpotListener, LocationsAdapter.OnLocationClickListener {
    private PLManager plManager;
    private int currentIndex = -1;
    private ViewPager2 locationsPager;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private View fadeOverlay;
    private TextView locationTitle, locationDescription, locationTitleOverlay;
    private FloatingActionButton fabMenu;
    private ChipGroup featuresChipGroup;
    private final Map<Integer, LocationData> locationDataMap = new HashMap<>();
    private List<LocationData> locationsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_tour);  // Add this line first

        // Initialize PLManager after setting content view
        plManager = new PLManager(this);
        plManager.setContentView(findViewById(R.id.contentView));
        plManager.onCreate();

        // Initialize other views and setup
        initializeViews();
        initializeLocationData();
        setupPLManager();
        setupBottomSheet();
        setupLocationsPager();
        setupFabMenu();

        // Load initial panorama with a slight delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> changePanorama(0), 50);
    }

    private void initializeViews() {
        fadeOverlay = findViewById(R.id.fadeOverlay);
        locationTitle = findViewById(R.id.locationTitle);
        locationDescription = findViewById(R.id.locationDescription);
        locationTitleOverlay = findViewById(R.id.locationTitleOverlay);
        locationsPager = findViewById(R.id.locationsPager);
        fabMenu = findViewById(R.id.fabMenu);
        featuresChipGroup = findViewById(R.id.featuresChipGroup);

        // Initialize bottom sheet behavior
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    private void initializeLocationData() {
        // Example: Reception Area
        LocationData reception = new LocationData(
                R.raw.office_360,
                "Reception Area",
                "Welcome to our modern reception area, featuring contemporary design and comfortable seating.",
                R.drawable.ic_building,
                5f, 0f
        );
        reception.features.add("24/7 Security");
        reception.features.add("Waiting Area");
        reception.features.add("Information Desk");
        reception.hotspots.add(new HotspotData(
                100L, 0.5f, 0.7f,
                "Proceed to Meeting Room",
                R.raw.hotspot,
                1
        ));
        locationDataMap.put(0, reception);

        // Example: Meeting Room
        LocationData meetingRoom = new LocationData(
                R.raw.sighisoara_sphere,
                "Main Conference Room",
                "State-of-the-art conference room equipped with the latest presentation technology.",
                R.drawable.ic_building,
                5f, 90f
        );
        meetingRoom.features.add("4K Projector");
        meetingRoom.features.add("Video Conferencing");
        meetingRoom.features.add("Seating for 20");
        meetingRoom.hotspots.add(new HotspotData(
                101L, 0.3f, 0.5f,
                "View Work Area",
                R.raw.hotspot,
                2
        ));
        locationDataMap.put(1, meetingRoom);

        locationsList = new ArrayList<>(locationDataMap.values());
    }

    private void setupPLManager() {
        // Basic settings
        plManager.setAccelerometerSensitivity(0.06f);  // Reduced sensitivity for stability
        plManager.setAccelerometerLeftRightEnabled(true);
        plManager.setAccelerometerUpDownEnabled(true);

        // Keep disabled if using manual controls
        plManager.setInertiaEnabled(true);         // Smooth scrolling effect
        plManager.setZoomEnabled(true);            // Enable pinch to zoom

        // Additional control settings
        plManager.setResetEnabled(true);           // Enable reset with 3 finger touch or shake
        plManager.setAcceleratedTouchScrollingEnabled(true); // Enable gyroscope/sensor based rotation
        plManager.setScrollingEnabled(true);       // Enable touch scrolling


        // Camera settings
        PLICamera camera = plManager.getPanorama().getCamera();
        camera.setFovRange(new PLRange(35.0f, 85.0f)); // Set field of view range
        camera.setRotation(-85.0f, 85.0f);
        camera. setFov(70.0f);
        camera.lookAt(0.0f, 0.0f);




        plManager.setAccelerometerSensitivity(0.06f);
        plManager.setAccelerometerLeftRightEnabled(true);
        plManager.setAccelerometerUpDownEnabled(true);


        // Set up animation properties

        plManager.setAnimationInterval(1.0f / 60.0f);
        plManager.setAnimationFrameInterval(1);

        plManager.setAcceleratedTouchScrollingEnabled(true);
        camera.lookAt(0.0f, 0.0f);                // Initial camera angle
        camera.setFov(75.0f);                     // Initial field of view

        // Setup hotspot listener
        try {
            Method setListenerMethod = PLManager.class.getMethod("setHotspotTouchListener", HotSpotListener.class);
            setListenerMethod.invoke(plManager, this);

        } catch (Exception e) {
            Timber.tag("PLManager").e(e, "Error setting up listeners");
        }
    }

    private void setupLocationsPager() {
        LocationsAdapter adapter = new LocationsAdapter(locationsList, this);
        locationsPager.setAdapter(adapter);
        locationsPager.setOffscreenPageLimit(3);

        locationsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (currentIndex != position) {
                    changePanorama(position);
                }
            }
        });
    }

    private void setupBottomSheet() {
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    locationTitleOverlay.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    locationTitleOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                locationTitleOverlay.setAlpha(1 - slideOffset);
            }
        });
    }

    private void setupFabMenu() {
        fabMenu.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void changePanorama(int index) {
        if (currentIndex == index) return;

        LocationData locationData = locationDataMap.get(index);
        if (locationData == null) return;

        currentIndex = index;
        locationsPager.setCurrentItem(index, false);

        fadeOverlay.setVisibility(View.VISIBLE); // Always show the overlay initially
        fadeOverlay.setAlpha(1f);

        try {
            loadNewPanorama(locationData);
            updateLocationInfo(locationData);
        } catch (Exception e) {
            Timber.tag("Panorama").e(e, "Error loading panorama: ");
        }
    }

    private void loadNewPanorama(LocationData locationData) {
        if (locationData == null) {
            Timber.tag("Panorama").e("LocationData is null, cannot load panorama.");
            return;
        }

        try {
            PLSphericalPanorama panorama = new PLSphericalPanorama();
            Bitmap bitmap = PLUtils.getBitmap(this, locationData.resourceId);
            if (bitmap == null) {
                Timber.tag("Bitmap").e("Failed to load bitmap for resource: %s", locationData.resourceId);
                return;
            }
            panorama.setImage(new PLImage(bitmap, false));
            panorama.getCamera().lookAt(locationData.defaultPitch, locationData.defaultYaw);

            for (HotspotData hotspot : locationData.hotspots) {
                try {
                    PLIImage hotspotImage = new PLImage(
                            BitmapFactory.decodeResource(getResources(), hotspot.iconResourceId)
                    );
                    ActionPLHotspot plHotspot = new ActionPLHotspot(
                            this,
                            hotspot.id,
                            hotspotImage,
                            hotspot.pitch,
                            hotspot.yaw,
                            1.0f,
                            1.0f
                    );
                    panorama.addHotspot(plHotspot);
                } catch (Exception e) {
                    Timber.tag("Hotspot").e(e, "Error adding hotspot: ");
                }
            }

            plManager.setPanorama(panorama);

            // Ensure the overlay fades out after setting the panorama
            fadeOverlay.animate()
                    .alpha(0f)
                    .setDuration(3)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fadeOverlay.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {
            Timber.tag("Panorama").e(e, "Error setting panorama: ");
            fadeOverlay.setVisibility(View.GONE);
        }
    }


    private void updateLocationInfo(LocationData locationData) {
        locationTitle.setText(locationData.title);
        locationTitleOverlay.setText(locationData.title);
        locationDescription.setText(locationData.description);
        updateFeatureChips(locationData.features);
    }

    private void updateFeatureChips(List<String> features) {
        featuresChipGroup.removeAllViews();
        for (String feature : features) {
            Chip chip = new Chip(this);
            chip.setText(feature);
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(ContextCompat.getColor(this, R.color.chip_text));
            featuresChipGroup.addView(chip);
        }
    }

    @Override
    public void onHotspotClick(long hotspotId) {
        Toast.makeText(this, "Hotspot clicked: " + hotspotId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationClick(int position) {
        changePanorama(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }
}
