package com.example.bankassistadmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aniketjain.weatherapp.R;


public class SOSFragment extends Fragment {


    WebView simpleWebView;

    public SOSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_s_o_s, container, false);

        simpleWebView = (WebView) v.findViewById(R.id.simpleWebView);

        simpleWebView.setWebViewClient(new MyWebViewClient());
        String url = "https://www.india.gov.in/spotlight/rescue-and-relief-uttarakhand";
        simpleWebView.getSettings().setJavaScriptEnabled(true);
        simpleWebView.loadUrl(url); // load a web page in a web view

        return v;

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}