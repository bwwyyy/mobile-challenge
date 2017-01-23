package com.android.philip.photoapp;

import android.app.Activity;
import android.content.Intent;
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
import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements XAuth500pxTask.Delegate {
    private static final String TAG = "MainActivity";
    private static final int STATIC_INTEGER_VALUE = 12345;

    private static String mUsername;
    private AccessToken mAccessToken;
    public static PxApi mPxApi;

    private RecyclerView mRecyclerView;
    private ImageAdapter mImgAdapter;

    private static ImgStore mImgStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = "";
        mImgStore = new ImgStore();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);


        // Main activity container
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mImgAdapter = new ImageAdapter(this, mImgStore, new PhotoOnClickListener());
        final GreedoLayoutManager layoutManager = new GreedoLayoutManager(mImgAdapter);
        layoutManager.setMaxRowHeight(MeasUtils.dpToPx(150, this));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mImgAdapter);

        int spacing = MeasUtils.dpToPx(4, this);
        mRecyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));


        // Login
        this.mPxApi = null;
        /*
        XAuth500pxTask loginTask = new XAuth500pxTask(this);
        try {
            this.mAccessToken = loginTask.execute(getString(R.string.consumer_key), getString(R.string.consumer_secret),
                    "", "").get();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        this.mPxApi = new PxApi(this.mAccessToken, getString(R.string.consumer_key), getString(R.string.consumer_secret));
        */


        Intent logInIntent = new Intent(this, LoginActivity.class);
        MainActivity.this.startActivityForResult(logInIntent, STATIC_INTEGER_VALUE);
    }

    public class PhotoOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int idx = mRecyclerView.getChildLayoutPosition(v);

            Intent fullScreenIntent = new Intent(v.getContext(), FullScreenImgActivity.class);
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.INDEX), idx);
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.CACHE), mImgStore.getImgNames());
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.URL), mImgStore.getImgs());

            MainActivity.this.startActivity(fullScreenIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    mUsername = data.getStringExtra(getString(R.string.USERNAME));
                    mAccessToken = (AccessToken) data.getExtras().get((getString(R.string.ACCESS_TOKEN)));
                    this.mPxApi = new PxApi(this.mAccessToken, getString(R.string.consumer_key), getString(R.string.consumer_secret));

                    refreshImgStorage();
                }
                break;
            }
        }
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
                if (in == -1)
                    Toast.makeText(getApplicationContext(), "Not Login yet!",Toast.LENGTH_SHORT).show();
                mImgAdapter.updateDataSet(mImgStore);
                mImgAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mImgAdapter);
            }

        }.execute(url);
    }
}
