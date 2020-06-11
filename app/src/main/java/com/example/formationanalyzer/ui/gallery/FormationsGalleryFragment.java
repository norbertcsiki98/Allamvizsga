package com.example.formationanalyzer.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formationanalyzer.AnalyzeFragment;
import com.example.formationanalyzer.R;

import java.util.ArrayList;
import java.util.List;

public class FormationsGalleryFragment extends Fragment {

    RecyclerView recyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArrayList<String> images = new ArrayList<>();
        images.add("lineup343");
        images.add("lineup4222");
        images.add("lineup4231");
        images.add("lineup433");
        images.add("lineup442");


        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView=view.findViewById(R.id.my_recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(),3);

        recyclerView.setLayoutManager(layoutManager);
        GalleryAdapter mAdapter = new GalleryAdapter(images, view.getContext(), null);
        recyclerView.setAdapter(mAdapter);


        return view;
    }
}