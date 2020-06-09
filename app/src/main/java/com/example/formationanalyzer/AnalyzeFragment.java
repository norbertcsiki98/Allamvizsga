package com.example.formationanalyzer;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.formationanalyzer.ui.gallery.GalleryFragment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AnalyzeFragment extends Fragment {
    public static final String TAG = AnalyzeFragment.class.getCanonicalName();

    ImageView img1;
    Button analyzeButton, resetButton;
    Bitmap bitmap;
    View colorView;
    int redColor, greenColor, blueColor;
    boolean isLoaded;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.analyze_fragment_main, container, false);
        img1 = v.findViewById(R.id.image);
        analyzeButton = v.findViewById(R.id.analyze_button);
        resetButton = v.findViewById(R.id.reset_button);
        colorView = v.findViewById(R.id.color);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        int height;
        if (getActivity() == null) {
            height = 300;
        } else {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.widthPixels;
        }
        img1.getLayoutParams().height = height;
        img1.setDrawingCacheEnabled(true);
        img1.buildDrawingCache(true);

        isLoaded = false;
        if (this.getArguments() != null) {
            String imageUrl = this.getArguments().getString("myname");
            Glide.with(v)
                    .asBitmap()
                    .load(getDrawableFromString(imageUrl,this.getContext()))
                    .placeholder(getResources().getDrawable(R.drawable.defaultpic))
                    .into(new BitmapImageViewTarget(img1) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);

                            isLoaded = true;
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);

                            isLoaded = false;
                        }
                    });
        }

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoaded) {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putBoolean("open_analyze", true);
                Fragment newFragment = new GalleryFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                //  ft.addToBackStack(null);
                ft.commit();
            }
        });

        img1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isLoaded) {
                    Log.d(TAG, "bitmap is null");
                    return false;
                }

                Log.d(TAG, "Action: " + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    bitmap = img1.getDrawingCache();
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    redColor = Color.red(pixel);
                    greenColor = Color.green(pixel);
                    blueColor = Color.blue(pixel);

                    colorView.setBackgroundColor(Color.rgb(redColor, greenColor, blueColor));
                }

                return false;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = null;
                Glide.with(img1.getContext()).clear(img1);
                colorView.setBackgroundColor(Color.TRANSPARENT);
                isLoaded = false;
            }
        });

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectColors();
            }
        });

        return v;
    }

    public int getDrawableFromString(String imageName, Context context) {
        if (TextUtils.isEmpty(imageName)) {
            return 0;
        }

        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(imageName, "drawable",
                context.getPackageName());

        return resourceId;
    }

    private void detectColors() {
        if (!isLoaded) {
            Log.d(TAG, "The bitmap is null.");
            return;
        }

        bitmap = img1.getDrawingCache();
        Log.d(TAG, "detect colors");
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

        Mat hsvImage = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, hsvImage, Imgproc.COLOR_RGB2HSV);

        float[] hsvColor = new float[3];
        Color.RGBToHSV(redColor, greenColor, blueColor, hsvColor);
        Log.d(TAG, "HSV: " + hsvColor[0] + " - " + hsvColor[1] + " - " + hsvColor[2]);

        Mat lowerRedHueRange = new Mat();
        int lowerHue = (int) (hsvColor[0] / 2 - 10);
        int upperHue = (int) (hsvColor[0] / 2 + 10);
        Core.inRange(hsvImage, new Scalar(lowerHue < 0 ? 0 : lowerHue, hsvColor[1] * 100, hsvColor[2] * 100),
                new Scalar(upperHue < 180 ? upperHue : 180, hsvColor[1] * 255, hsvColor[2] * 255), lowerRedHueRange);

        Mat kernel = new Mat(new Size(1, 1), CvType.CV_8UC1, new Scalar(hsvColor[0]));
        Imgproc.morphologyEx(lowerRedHueRange, lowerRedHueRange, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(lowerRedHueRange, lowerRedHueRange, Imgproc.MORPH_DILATE, kernel);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(lowerRedHueRange, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        contours.sort(new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return Double.compare(Imgproc.contourArea(o2), Imgproc.contourArea(o1));
            }
        });

        List<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            Log.d(TAG, "Area: " + Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 30) {
                Scalar colour = new Scalar(90, 255, 255);
                Imgproc.drawContours(lowerRedHueRange, contours, i, colour, -1);
                MatOfInt hull = new MatOfInt();
                Imgproc.convexHull(contour, hull);

                List<Point> l = new ArrayList<>();
                l.clear();
                double sum_x = 0;
                double sum_y = 0;
                int j;
                for (j = 0; j < hull.size().height; j++) {
                    l.add(contour.toList().get(hull.toList().get(j)));
                    sum_x += l.get(j).x;
                    sum_y += l.get(j).y;
                }

                int x = (int) (sum_x / j);
                int y = (int) (sum_y / j);
                coordinates.add(Pair.create(x, y));
            } else {
                break;
            }
        }

        List<Pair<Integer, Integer>> finalCoordinates = new ArrayList<>();

        for (int i = 0; i < coordinates.size(); i++) {
            boolean isGood = true;
            for (int j = 0; j < finalCoordinates.size(); j++) {
                if (Math.abs(coordinates.get(i).first - finalCoordinates.get(j).first) < 20
                        || Math.abs(coordinates.get(i).second - finalCoordinates.get(j).second) < 20) {
                    isGood = false;
                    break;
                }
            }
            if (isGood) {
                finalCoordinates.add(coordinates.get(i));
            }
        }

        for (int i = 0; i < finalCoordinates.size(); i++) {
            Log.d(TAG, i + ". item x coordinate: " + finalCoordinates.get(i).first + "; y coordinate: " + finalCoordinates.get(i).second);
        }

        Utils.matToBitmap(lowerRedHueRange, bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        img1.setImageBitmap(mutableBitmap);
    }
}
