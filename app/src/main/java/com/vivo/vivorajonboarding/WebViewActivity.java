package com.vivo.vivorajonboarding;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vivo.vivorajonboarding.common.NetworkChangeListener;

public class WebViewActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn, downloadBtn;
    WebView webView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initialization();

        //handle back button
        backBtn.setOnClickListener(view -> onBackPressed());

        //set up webView
        setUpWebView();

        //handle swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            setUpWebView();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setUpWebView() {
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        backBtn = findViewById(R.id.backBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}