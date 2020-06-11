package com.example.formationanalyzer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ResultFragment extends Fragment {

    public static final String TAG = ResultFragment.class.getCanonicalName();

    ImageView selectedImageview, resultFormatioImageview;
    String imageUrl;
    String selectedFormation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.result_fragment, container, false);

        selectedImageview = view.findViewById(R.id.selected_picture);
        resultFormatioImageview = view.findViewById(R.id.result_formation_picture);

        if (this.getArguments() != null) {
            imageUrl = this.getArguments().getString("selectedimage");
            selectedFormation = this.getArguments().getString("selectedformation");

            int id = getDrawableFromString(imageUrl, view.getContext());
            Glide.with(view)
                    .load(id != 0 ? id : imageUrl)
                    .placeholder(getResources().getDrawable(R.drawable.defaultpic))
                    .into(selectedImageview);
            Glide.with(view)
                    .load(getDrawableFromString(selectedFormation,view.getContext()))
                    .placeholder(getResources().getDrawable(R.drawable.defaultpic))
                    .into(resultFormatioImageview);
        }

        return view;

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
}
