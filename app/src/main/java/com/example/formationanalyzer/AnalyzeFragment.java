package com.example.formationanalyzer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.formationanalyzer.ui.gallery.FormationsGalleryFragment;
import com.example.formationanalyzer.ui.gallery.GalleryFragment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class AnalyzeFragment extends Fragment {
    public static final String TAG = AnalyzeFragment.class.getCanonicalName();

    ImageView img1, img2;
    Button defaultFormationsButton;
    Button analyzebutton;
    Bitmap bitmap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.analyze_fragment_main, container, false);
        img1 = v.findViewById(R.id.imageView1);
        img2 = v.findViewById(R.id.imageView2);
        defaultFormationsButton = v.findViewById(R.id.defaultformationsbutton);
        analyzebutton = v.findViewById(R.id.analyzebutton);

        if (this.getArguments() != null) {
            Glide.with(v)
                    .asBitmap()
                    .load(this.getArguments().getInt("myname"))
                    .into(new BitmapImageViewTarget(img1) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);

                            bitmap = resource;
                        }
                    });
        }

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new GalleryFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                //  ft.addToBackStack(null);
                ft.commit();
            }
        });

        defaultFormationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FormationsGalleryFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        analyzebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectColors();
            }
        });

        return v;
    }

    private void detectColors() {
        if (bitmap == null) {
            Log.d(TAG, "The bitmap is null.");
        }

        Log.d(TAG, "detect colors");
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(),
                CvType.CV_8UC3);

        Mat hsv_image = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, hsv_image, Imgproc.COLOR_BGR2HSV);

        Mat lower_red_hue_range = new Mat();
        Mat upper_red_hue_range = new Mat();

        Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
        Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);
        Utils.matToBitmap(hsv_image, bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        img2.setImageBitmap(mutableBitmap);
    }
}
