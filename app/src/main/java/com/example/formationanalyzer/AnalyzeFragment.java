package com.example.formationanalyzer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.formationanalyzer.ui.gallery.FormationsGalleryFragment;
import com.example.formationanalyzer.ui.gallery.GalleryFragment;


public class AnalyzeFragment extends Fragment {

    ImageView img1, img2;
    Button defaultFormationsButton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.analyze_fragment_main, container, false);
        img1 = v.findViewById(R.id.imageView1);
        img2 = v.findViewById(R.id.imageView2);
        defaultFormationsButton = v.findViewById(R.id.defaultformationsbutton);

        if (this.getArguments() != null) {
            // Log.d("aaa",this.getArguments().getString("myname"));
            Glide.with(v).load(this.getArguments().getInt("myname")).into(img1);
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

        return v;
    }
}
