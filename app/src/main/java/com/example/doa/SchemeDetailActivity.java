package com.example.doa;

import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchemeDetailActivity extends AppCompatActivity {

    private WebView webView;
    private CardView eligibilityCard, howToApplyCard, selectionProcessCard, additionalInfoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_scheme_detail);

        // Fetch data from Intent
        String schemeName = getIntent().getStringExtra("scheme_name");
        String schemeVideo = getIntent().getStringExtra("scheme_video");
        String schemeContent = getIntent().getStringExtra("scheme_content");
        String eligibilityContentText = getIntent().getStringExtra("eligibility_content");
        String howToApplyContentText = getIntent().getStringExtra("how_to_apply_content");
        String selectionProcessText = getIntent().getStringExtra("selection_process");
        String additionalInfoText = getIntent().getStringExtra("additional_info");
        int logoResource = getIntent().getIntExtra("scheme_logo", R.drawable.logo);

        // Initialize views
        TextView schemeNameTextView = findViewById(R.id.scheme_name);
        ImageView schemeLogoImageView = findViewById(R.id.scheme_logo);
        TextView schemeContentTextView = findViewById(R.id.scheme_content);
        schemeNameTextView.setText(schemeName);
        schemeLogoImageView.setImageResource(logoResource);
        schemeContentTextView.setText(schemeContent);

        // Initialize WebView for YouTube video
        webView = findViewById(R.id.youtube_webview);
        initWebView(schemeVideo);

        // Initialize buttons and content CardViews
        Button eligibilityButton = findViewById(R.id.eligibility_button);
        Button howToApplyButton = findViewById(R.id.how_to_apply_button);
        Button selectionProcessButton = findViewById(R.id.selection_process_button);
        Button additionalInfoButton = findViewById(R.id.additional_info_button);

        eligibilityCard = findViewById(R.id.eligibility_card);
        howToApplyCard = findViewById(R.id.how_to_apply_card);
        selectionProcessCard = findViewById(R.id.selection_process_card);
        additionalInfoCard = findViewById(R.id.additional_info_card);

        // Set content for each section
        ((TextView) findViewById(R.id.eligibility_content)).setText(eligibilityContentText);
        ((TextView) findViewById(R.id.how_to_apply_content)).setText(howToApplyContentText);
        ((TextView) findViewById(R.id.selection_process_content)).setText(selectionProcessText);
        ((TextView) findViewById(R.id.additional_info_content)).setText(additionalInfoText);

        // Set click listeners for buttons
        eligibilityButton.setOnClickListener(view -> toggleVisibility(eligibilityCard));
        howToApplyButton.setOnClickListener(view -> toggleVisibility(howToApplyCard));
        selectionProcessButton.setOnClickListener(view -> toggleVisibility(selectionProcessCard));
        additionalInfoButton.setOnClickListener(view -> toggleVisibility(additionalInfoCard));
    }

    private void initWebView(String url) {
        if (url != null && !url.trim().isEmpty()) {
            String videoId = extractYouTubeVideoId(url);
            if (videoId != null && !videoId.isEmpty()) {
                webView.setWebViewClient(new WebViewClient());
                webView.setWebChromeClient(new WebChromeClient());

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setMediaPlaybackRequiresUserGesture(false);
                webSettings.setAllowFileAccess(true);
                webSettings.setAllowContentAccess(true);

                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

                String iframeHtml = "<!DOCTYPE html><html><head>"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<style>html,body{margin:0;padding:0;width:100%;height:100%;background-color:#000000;} .video-container{position:relative;padding-bottom:56.25%;height:0;overflow:hidden;} .video-container iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0;}</style>"
                        + "</head><body>"
                        + "<div class=\"video-container\">"
                        + "<iframe src=\"https://www.youtube.com/embed/" + videoId + "?enablejsapi=1&origin=https://www.youtube.com&playsinline=1&rel=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
                        + "</div>"
                        + "</body></html>";

                webView.loadDataWithBaseURL("https://www.youtube.com", iframeHtml, "text/html", "utf-8", null);
            }
        }
    }

    private String extractYouTubeVideoId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        url = url.trim();

        if (url.matches("^[a-zA-Z0-9_-]{11}$")) {
            return url;
        }

        Pattern pattern = Pattern.compile(
                "(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        if (url.contains("/embed/")) {
            String[] parts = url.split("/embed/");
            if (parts.length > 1) {
                String videoId = parts[1];
                if (videoId.contains("?")) {
                    videoId = videoId.substring(0, videoId.indexOf("?"));
                }
                if (videoId.contains("&")) {
                    videoId = videoId.substring(0, videoId.indexOf("&"));
                }
                return videoId;
            }
        }

        return null;
    }

    private void toggleVisibility(CardView selectedCard) {
        // Hide all cards first
        eligibilityCard.setVisibility(View.GONE);
        howToApplyCard.setVisibility(View.GONE);
        selectionProcessCard.setVisibility(View.GONE);
        additionalInfoCard.setVisibility(View.GONE);

        // Show the selected card
        if (selectedCard.getVisibility() == View.GONE) {
            selectedCard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }
}
