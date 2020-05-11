package com.example.docbaobangrss;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.URL;
import java.util.HashMap;

public class ContentsFragment extends Fragment {
    public static final String ARG_KEY_URL = "arg_key_url";

    public static ContentsFragment newInstance(String url) {
        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_URL, url);
        ContentsFragment fragment = new ContentsFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    private View rootView;
    private HashMap<String, ContentsTmp> dataTmp = new HashMap();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trangchu, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        if (getArguments() != null) {
            String urlRss = getArguments().getString(ARG_KEY_URL, "");
            Log.d("initView", urlRss);
        }
    }

    public void refreshData(String urlRss) {
        Log.d("refreshData", urlRss);
    }
}
