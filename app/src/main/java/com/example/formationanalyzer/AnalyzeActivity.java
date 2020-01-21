package com.example.formationanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class AnalyzeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
        frag_trans.add(R.id.fragment_container,new AnalyzeFragment());
        frag_trans.commit();
    }
}
