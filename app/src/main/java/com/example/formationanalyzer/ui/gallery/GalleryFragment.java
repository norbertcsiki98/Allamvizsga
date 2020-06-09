package com.example.formationanalyzer.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formationanalyzer.AnalyzeFragment;
import com.example.formationanalyzer.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG = GalleryFragment.class.getCanonicalName();
    private GalleryAdapter mAdapter;
    GalleryInterface imageselected = new GalleryInterface() {
        @Override
        public void itemSelected(String name) {
            if (openAnalyze) {
                Fragment newFragment = new AnalyzeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                ft.commit();
                Bundle b = new Bundle();
                b.putString("myname", name);
                newFragment.setArguments(b);
            }
        }
    };

    private Button addImageButton;
    private RecyclerView recyclerView;
    private boolean openAnalyze;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            openAnalyze = getArguments().getBoolean("open_analyze");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.my_recyclerView);
        addImageButton = view.findViewById(R.id.addImageButton);



        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(v.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });


        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 3);

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new GalleryAdapter(GalleryImagesSingleton.getInstance().getArray() , view.getContext(), imageselected);
        recyclerView.setAdapter(mAdapter);


        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK &&  requestCode == IMAGE_PICK_CODE){
            GalleryImagesSingleton.getInstance().addToArray(data.getData().toString());
            mAdapter.notifyDataSetChanged();
            Log.d(TAG,"Image: " + data.getData());
        }
    }
}