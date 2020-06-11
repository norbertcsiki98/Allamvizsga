package com.example.formationanalyzer.ui.gallery;

import java.util.ArrayList;

public class GalleryImagesSingleton {

    private static GalleryImagesSingleton mInstance;
    private ArrayList<String> images = null;

    public static GalleryImagesSingleton getInstance() {
        if (mInstance == null)
            mInstance = new GalleryImagesSingleton();

        return mInstance;
    }

    private GalleryImagesSingleton() {
        images = new ArrayList<>();
        images.add("real_image");
        images.add("real_image2");
        images.add("test_image");
        images.add("real_image3");
        images.add("real_image5");
        images.add("real_image7");
        images.add("real_image8");
        images.add("real_image9");
        images.add("real_image10");

    }

    // retrieve array from anywhere
    public ArrayList<String> getArray() {
        return this.images;
    }

    //Add element to array
    public void addToArray(String value) {
        images.add(value);
    }
}
