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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnalyzeFragment extends Fragment {
    public static final String TAG = AnalyzeFragment.class.getCanonicalName();

    ImageView img1;
    Button analyzeButton, resetButton;
    Bitmap bitmap;
    View colorView;
    int redColor, greenColor, blueColor;
    boolean isLoaded;
    private Map<String, List<Pair<Integer, Integer>>> defaultFormations = new HashMap<>();
    String imageUrl;
    String selectedFormation = "";

    public AnalyzeFragment() {
        createDefaultFormations();
    }

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
            imageUrl = this.getArguments().getString("myname");
            int id = getDrawableFromString(imageUrl, v.getContext());
            Glide.with(v)
                    .asBitmap()
                    .load(id != 0 ? id : imageUrl)
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
                Fragment newFragment = new ResultFragment();
                Bundle b = new Bundle();
                b.putString("selectedimage",imageUrl);
                b.putString("selectedformation",selectedFormation);
                newFragment.setArguments(b);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                ft.addToBackStack(null);
                ft.commit();


            }
        });

        return v;
    }

    private void createDefaultFormations() {
        // 4-3-3
        List<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        coordinates.add(Pair.create(0, 0));
        coordinates.add(Pair.create(0, 33));
        coordinates.add(Pair.create(0, 66));
        coordinates.add(Pair.create(0, 100));
        coordinates.add(Pair.create(46, 16));
        coordinates.add(Pair.create(46, 50));
        coordinates.add(Pair.create(46, 84));
        coordinates.add(Pair.create(97, 15));
        coordinates.add(Pair.create(100, 50));
        coordinates.add(Pair.create(97, 85));
        defaultFormations.put("lineup433", coordinates);

        //3-4-3
        coordinates = new ArrayList<>();
        coordinates.add(Pair.create(0, 20));
        coordinates.add(Pair.create(0, 50));
        coordinates.add(Pair.create(0, 80));
        coordinates.add(Pair.create(52, 0));
        coordinates.add(Pair.create(52, 30));
        coordinates.add(Pair.create(52, 70));
        coordinates.add(Pair.create(52, 100));
        coordinates.add(Pair.create(94, 0));
        coordinates.add(Pair.create(100, 50));
        coordinates.add(Pair.create(94, 100));
        defaultFormations.put("lineup343", coordinates);

        //4-4-2
        coordinates = new ArrayList<>();
        coordinates.add(Pair.create(0, 0));
        coordinates.add(Pair.create(0, 33));
        coordinates.add(Pair.create(0, 66));
        coordinates.add(Pair.create(0, 100));
        coordinates.add(Pair.create(47, 0));
        coordinates.add(Pair.create(47, 33));
        coordinates.add(Pair.create(47, 66));
        coordinates.add(Pair.create(47, 100));
        coordinates.add(Pair.create(100, 33));
        coordinates.add(Pair.create(100, 66));
        defaultFormations.put("lineup442", coordinates);

        //4-2-3-1
        coordinates = new ArrayList<>();
        coordinates.add(Pair.create(0, 0));
        coordinates.add(Pair.create(0, 33));
        coordinates.add(Pair.create(0, 66));
        coordinates.add(Pair.create(0, 100));
        coordinates.add(Pair.create(37, 29));
        coordinates.add(Pair.create(37, 67));
        coordinates.add(Pair.create(68, 8));
        coordinates.add(Pair.create(68, 51));
        coordinates.add(Pair.create(68, 100));
        coordinates.add(Pair.create(68, 51));
        defaultFormations.put("lineup4231", coordinates);

        //4-2-2-2
        coordinates = new ArrayList<>();
        coordinates.add(Pair.create(0, 0));
        coordinates.add(Pair.create(0, 33));
        coordinates.add(Pair.create(0, 66));
        coordinates.add(Pair.create(0, 100));
        coordinates.add(Pair.create(48, 33));
        coordinates.add(Pair.create(48, 66));
        coordinates.add(Pair.create(78, 0));
        coordinates.add(Pair.create(78, 100));
        coordinates.add(Pair.create(100, 33));
        coordinates.add(Pair.create(100, 66));
        defaultFormations.put("lineup4222", coordinates);
    }


    private int getDrawableFromString(String imageName, Context context) {
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
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);

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

        Mat imageAfterErode = new Mat();
        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(1, 1));
        Imgproc.erode(lowerRedHueRange, imageAfterErode, erode);

        Mat imageAfterDilate = new Mat();
        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(1, 1));
        Imgproc.dilate(imageAfterErode, imageAfterDilate, dilate);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(imageAfterDilate, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
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
            //  if (Imgproc.contourArea(contours.get(i)) > 30) {
            Scalar colour = new Scalar(90, 255, 255);
            Imgproc.drawContours(imageAfterDilate, contours, i, colour, -1);
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
//            } else {
//                break;
//            }
        }

        List<Pair<Integer, Integer>> finalCoordinates = new ArrayList<>();

        for (int i = 0; i < coordinates.size(); i++) {
            Log.d(TAG, i + ".coordinate: " + coordinates.get(i).first + " - " + coordinates.get(i).second);
            boolean isGood = true;
            for (int j = 0; j < finalCoordinates.size(); j++) {
                if (Math.abs(coordinates.get(i).first - finalCoordinates.get(j).first) < 100
                        && Math.abs(coordinates.get(i).second - finalCoordinates.get(j).second) < 100) {
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

//        Utils.matToBitmap(lowerRedHueRange, bitmap);
//        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//        img1.setImageBitmap(mutableBitmap);

        checkFormations(finalCoordinates);
    }

    private void checkFormations(List<Pair<Integer, Integer>> finalCoordinates) {
        if (finalCoordinates.size() == 0) {
            return;
        }

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
        for (int i = 0; i < finalCoordinates.size(); i++) {
            Pair<Integer, Integer> coordinate = finalCoordinates.get(i);

            if (coordinate.first < minX) {
                minX = coordinate.first;
            }

            if (coordinate.second < minY) {
                minY = coordinate.second;
            }

            if (coordinate.first > maxX) {
                maxX = coordinate.first;
            }

            if (coordinate.second > maxY) {
                maxY = coordinate.second;
            }
        }

        List<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        for (int i = 0; i < finalCoordinates.size(); i++) {
            Pair<Integer, Integer> coordinate = finalCoordinates.get(i);

            coordinates.add(Pair.create(coordinate.first - minX, coordinate.second - minY));
        }

        maxX -= minX;
        maxY -= minY;

        if (maxX == 0 || maxY == 0) {
            return;
        }

        List<Pair<Integer, Integer>> coordinatesInPercentage = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            Pair<Integer, Integer> coordinate = coordinates.get(i);

            int xInPercentage = coordinate.first * 100 / maxX;
            int yInPercentage = coordinate.second * 100 / maxY;

            coordinatesInPercentage.add(Pair.create(xInPercentage, yInPercentage));
        }

        for (int i = 0; i < coordinatesInPercentage.size(); i++) {
            Log.d(TAG, "X: " + coordinatesInPercentage.get(i).first + " Y: " + coordinatesInPercentage.get(i).second);
        }

        Map<String, Integer> result = new HashMap<>();
        for (String key : defaultFormations.keySet()) {
            List<Pair<Integer, Integer>> lineUp = defaultFormations.get(key);

            int counter = 0;
            for (int j = 0; j < coordinatesInPercentage.size(); j++) {
                Pair<Integer, Integer> selectedCoordinate = coordinatesInPercentage.get(j);

                int removePosition = -1;
                List<Pair<Integer, Integer>> possibleSolutions = new ArrayList<>();
                for (int k = 0; k < lineUp.size(); k++) {
                    Pair<Integer, Integer> currentCoordinate = lineUp.get(k);
                    if (Math.abs(selectedCoordinate.first - currentCoordinate.first) < 15 &&
                            Math.abs(selectedCoordinate.second - currentCoordinate.second) < 15) {
                        possibleSolutions.add(Pair.create(k,
                                Math.abs(selectedCoordinate.first - currentCoordinate.first) +
                                        Math.abs(selectedCoordinate.second - currentCoordinate.second)));
                    }
                }

                int minDistance = Integer.MAX_VALUE;
                for (int k = 0; k < possibleSolutions.size(); k++) {
                    if (possibleSolutions.get(k).second < minDistance) {
                        removePosition = possibleSolutions.get(k).first;
                        minDistance = possibleSolutions.get(k).second;
                    }
                }

                if (removePosition != -1) {
                    lineUp.remove(removePosition);
                    counter++;
                }
            }

            if (counter > 0) {
                result.put(key, counter);
            }
        }

        int maxCount = 0;

        for (String key : result.keySet()) {
            int count = result.get(key);
            Log.d(TAG, "Formation name: " + key + " - count: " + count);

            if (count > maxCount) {
                selectedFormation = key;
                maxCount = count;
            }
        }

        Log.d(TAG, "Selected formation: " + selectedFormation);
    }
}
