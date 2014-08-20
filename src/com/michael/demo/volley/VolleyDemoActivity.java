package com.michael.demo.volley;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

/**
 * Created by michael on 2013/8/20.
 */
public class VolleyDemoActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = VolleyDemoActivity.class.getSimpleName();
    private final VolleyDemoActivity self = this;
    private RequestQueue mQueue;
    private long requestStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volley_demo_layout);

        mQueue = Volley.newRequestQueue(getApplicationContext());

        findViewById(R.id.volley_req).setOnClickListener(this);
        findViewById(R.id.http_client_req).setOnClickListener(this);
        findViewById(R.id.image_loading).setOnClickListener(this);
        findViewById(R.id.load_net_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.volley_req:
                requestVolley();
                break;
            case R.id.http_client_req:
                requestHttpClient();
                break;
            case R.id.image_loading:
                startActivity(new Intent(self, ImageLoadingActivity.class));
                break;
            case R.id.load_net_image:
                loadNetworkImageView();
                break;
        }
    }

    private void requestVolley() {
        // Volley 请求（似乎不支持https）
        String url = "http://api.map.baidu.com/geocoder?location=39,117&output=json";
        mQueue.add(new JsonObjectRequest(Method.GET, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        long time = System.currentTimeMillis() - requestStartTime;
                        Toast.makeText(VolleyDemoActivity.this, "Volley 请求耗时 : " + time, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Volley 请求耗时 : " + time);
                    }
                }, null
        ));
        requestStartTime = System.currentTimeMillis();
        mQueue.start();
    }

    private void requestHttpClient() {
        // HttpClient 请求（支持http和https）
        getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<JSONObject>() {
            @Override
            public Loader<JSONObject> onCreateLoader(int id, Bundle bundle) {
                requestStartTime = System.currentTimeMillis();
                return new JSONLoader(getApplicationContext());
            }

            @Override
            public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
                long time = System.currentTimeMillis() - requestStartTime;
                Toast.makeText(VolleyDemoActivity.this, "HttpClient 请求耗时 : " + time, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "HttpClient 请求耗时 : " + time);
                getSupportLoaderManager().destroyLoader(0);
            }

            @Override
            public void onLoaderReset(Loader<JSONObject> loader) {
            }
        });
    }

    private void loadNetworkImageView() {
        String url = "https://github.com/apple-touch-icon-144.png";
        NetworkImageView view = (NetworkImageView) findViewById(R.id.network_image_view);
        view.setImageUrl(null, null);
        view.setImageUrl(url, new ImageLoader(mQueue, new BitmapCache()));
    }

}
