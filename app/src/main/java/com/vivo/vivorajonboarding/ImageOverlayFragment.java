package com.vivo.vivorajonboarding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.vivo.vivorajonboarding.R;

public class ImageOverlayFragment extends Fragment {
    private static final String TAG = "ImageOverlayFragment";
    private static final String ARG_IMAGE_URL = "imageUrl";

    /**
     * Factory method to create a new instance of this fragment using the provided image URL.
     *
     * @param imageUrl URL of the image to be displayed.
     * @return A new instance of ImageOverlayFragment.
     */
    public static ImageOverlayFragment newInstance(String imageUrl) {
        ImageOverlayFragment fragment = new ImageOverlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable shared element transitions, ensuring compatibility
        if (getContext() != null) {
            setSharedElementEnterTransition(TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.move));
        } else {
            Log.e(TAG, "Context is null; transitions could not be set.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_overlay, container, false);

        // Find the PhotoView
        PhotoView photoView = view.findViewById(R.id.fullscreenImage);

        // Retrieve the image URL from the arguments
        String imageUrl = getArguments() != null ? getArguments().getString(ARG_IMAGE_URL) : null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageIntoPhotoView(photoView, imageUrl);
        } else {
            handleInvalidImageUrl();
        }

        // Set a click listener to close the fragment when the image is tapped
        photoView.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    /**
     * Loads an image into the provided PhotoView using Glide.
     *
     * @param photoView The PhotoView to load the image into.
     * @param imageUrl  The URL of the image to load.
     */
    private void loadImageIntoPhotoView(PhotoView photoView, String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_image) // Display a placeholder image while loading
                .error(R.drawable.placeholder_image) // Display a fallback image on error
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Image load failed: " + imageUrl, e);
                        Toast.makeText(requireContext(), R.string.error_invalid_image, Toast.LENGTH_SHORT).show();
                        return false; // Allow Glide to handle the error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image successfully loaded: " + imageUrl);
                        return false; // Allow Glide to handle further processing
                    }
                })
                .into(photoView);
    }

    /**
     * Handles cases where the provided image URL is invalid or null.
     */
    private void handleInvalidImageUrl() {
        Log.e(TAG, "Invalid or null image URL provided.");
        Toast.makeText(requireContext(), R.string.error_invalid_image, Toast.LENGTH_SHORT).show();
    }
}
