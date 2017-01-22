package com.android.philip.photoapp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.philip.photoapp.api.PxApi;
import com.android.philip.photoapp.api.XAuth500pxTask;
import com.android.philip.photoapp.api.auth.AccessToken;
import com.android.philip.photoapp.api.auth.FiveHundredException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements XAuth500pxTask.Delegate {
    private static final String TAG = "MainActivity";

    private Button mRefreshButton;
    private static String mUsername;
    private AccessToken mAccessToken;
    public static PxApi mPxApi;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = "";


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mRefreshButton = (Button) findViewById(R.id.load_button);


        // Login
        XAuth500pxTask loginTask = new XAuth500pxTask(this);
        try {
            this.mAccessToken = loginTask.execute(getString(R.string.consumer_key), getString(R.string.consumer_secret),
                    getString(R.string.user), getString(R.string.user_password)).get();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        this.mPxApi = new PxApi(this.mAccessToken, getString(R.string.consumer_key), getString(R.string.consumer_secret));
        this.mPxApi = null;
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "/photos?feature=user&username=&sort=created_at&image_size=20&include_store=store_download&include_states=voted";
                JSONObject res = mPxApi.get(url);
                JSONArray photos = null;
                try {
                    photos = res.getJSONArray("photos");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int len = photos.length();
                Log.d(TAG, "Load " + len + " images.");
            }
        });
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.w(TAG, "success "+result);

    }

    @Override
    public void onFail(FiveHundredException e) {}
}
