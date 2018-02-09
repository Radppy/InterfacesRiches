package fr.enssat.abaclet.interfacesriches;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onCreate(mapViewBundle);

        initVideo();
        initButtons();
        initWebView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MapView mMapView = findViewById(R.id.mapview);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle != null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MapView mMapView = findViewById(R.id.mapview);
        mMapView.onDestroy();
    }

    private void initVideo(){
        final VideoView mVideoView = (VideoView) findViewById(R.id.videoView);
        // Récupération de l'url dans le fichier json
        InputStream inputStream = getResources().openRawResource(R.raw.chapters);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr=inputStream.read();
            while(ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch(IOException e) {
            Log.e("Error : ",e.getMessage());
            e.printStackTrace();
        }

        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            String videoURL = jObject.getJSONObject("Film").getString("file_url");
            // Initialisation vidéo
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(mVideoView);
            Uri mVideoURI = Uri.parse(videoURL);
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(mVideoURI);
        } catch(Exception e) {
            Log.e("Error : ",e.getMessage());
            e.printStackTrace();
        }

        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
    }

    private void initButtons(){
        //Récupération des chapitres du fichier json
        InputStream inputStream = getResources().openRawResource(R.raw.chapters);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr=inputStream.read();
            while(ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch(IOException e) {
            Log.e("Error : ",e.getMessage());
            e.printStackTrace();
        }

        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            JSONArray jChaps = jObject.getJSONArray("Chapters");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int pos = 0;
            String title = "";
            LinearLayout chapters = (LinearLayout)findViewById(R.id.buttonsLayout);
            for(int i=0 ; i<jChaps.length() ; i++){
                pos = jChaps.getJSONObject(i).getInt("pos");
                title = jChaps.getJSONObject(i).getString("title");
                Button button = new Button(this);
                button.setTag(pos);
                button.setText(title);
                button.setLayoutParams(layoutParams);
                button.setOnClickListener(chapListener);
                chapters.addView(button);
            }
        } catch (Exception e){
            Log.e("Error : ",e.getMessage());
            e.printStackTrace();
        }

    }

    private View.OnClickListener chapListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = 1000*(int)view.getTag();
            VideoView mVideoView = (VideoView)findViewById(R.id.videoView);
            mVideoView.seekTo(position);
        }
    };

    private void initWebView(){
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        // Récupération URL dans le fichier json
        InputStream inputStream = getResources().openRawResource(R.raw.chapters);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr=inputStream.read();
            while(ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch(IOException e) {
            Log.e("Error : ",e.getMessage());
            e.printStackTrace();
        }

        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            String url = jObject.getJSONObject("Film").getString("synopsis_url");
            webView.loadUrl(url);
        }catch(Exception e) {
            Log.e("Error : ", e.getMessage());
            e.printStackTrace();
        }
    }
}
