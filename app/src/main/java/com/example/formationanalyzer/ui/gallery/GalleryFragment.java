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

public class GalleryFragment extends Fragment {



    GalleryInterface imageselected = new GalleryInterface() {
        @Override
        public void itemSelected(Integer name) {
            Fragment newFragment = new AnalyzeFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, newFragment );
            ft.commit();
            Bundle b = new Bundle();
            b.putInt("myname", name);
            newFragment.setArguments(b);

        }
    };
    RecyclerView recyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.formation3);
        images.add(R.drawable.formation4);
        images.add(R.drawable.formation5);
        images.add(R.drawable.real_image);
        images.add(R.drawable.test_image);
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView=view.findViewById(R.id.my_recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(),3);

        recyclerView.setLayoutManager(layoutManager);
        GalleryAdapter mAdapter = new GalleryAdapter(images, view.getContext(),imageselected);
        recyclerView.setAdapter(mAdapter);


        return view;
    }
}