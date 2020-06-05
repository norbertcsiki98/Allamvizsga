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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class AnalyzeFragment extends Fragment {
    public static final String TAG = AnalyzeFragment.class.getCanonicalName();

    ImageView img1;
    Button defaultFormationsButton;
    Button analyzebutton;
    Bitmap bitmap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.analyze_fragment_main, container, false);
        img1 = v.findViewById(R.id.imageView1);
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

                            bitmap = resource.copy(Bitmap.Config.ARGB_8888, true);
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
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

        Mat hsvImage = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, hsvImage, Imgproc.COLOR_RGB2HSV_FULL);

        Mat lowerRedHueRange = new Mat();
        Core.inRange(hsvImage, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lowerRedHueRange);

        Mat kernel = new Mat(new Size(1, 1), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(lowerRedHueRange, lowerRedHueRange, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(lowerRedHueRange, lowerRedHueRange, Imgproc.MORPH_DILATE, kernel);
        Utils.matToBitmap(lowerRedHueRange, bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        img1.setImageBitmap(mutableBitmap);

        //Imgproc.findContours();
    }
}
