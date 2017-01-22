package com.android.philip.photoapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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

    private static ImgStore mImgStore;

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
                    "", "").get();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        this.mPxApi = new PxApi(this.mAccessToken, getString(R.string.consumer_key), getString(R.string.consumer_secret));

        // Load Img
        mImgStore = new ImgStore();
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshImgStorage();
            }
        });
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.w(TAG, "success "+result);

    }

    @Override
    public void onFail(FiveHundredException e) {}

    private void refreshImgStorage () {
        String url = "/photos?feature=user&username=" + mUsername + "&sort=created_at&image_size=20&include_store=store_download&include_states=voted";

        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                if (mPxApi == null)
                    return -1;

                JSONObject res = null;
                try {
                    res = mPxApi.get(params[0]);
                    JSONArray photos = res.getJSONArray("photos");
                    int len = photos.length();
                    for (int i = 0; i < len; ++ i) {
                        JSONObject curr = photos.getJSONObject(i);
                        String key = curr.getString("id");
                        mImgStore.addImage(key, curr);
                    }

                    Log.d(TAG, "Load " + mImgStore.size() + " images.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer in) {

            }

        }.execute(url);
    }
}
