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
import android.widget.Toast;

import com.android.philip.photoapp.api.PxApi;
import com.android.philip.photoapp.api.XAuth500pxTask;
import com.android.philip.photoapp.api.auth.AccessToken;
import com.android.philip.photoapp.api.auth.FiveHundredException;
import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements XAuth500pxTask.Delegate {
    private static final String TAG = "MainActivity";
    private static final int LOGIN_ACTIVITY_INT = 0;
    private static final int IMG_DETAIL_ACTIVITY_INT = 1;
    private static final int NUMBER_OG_IMG_IN_ONE_SCREEN = 6;

    private static String mUsername;
    private AccessToken mAccessToken;
    public static PxApi mPxApi;

    private RecyclerView mRecyclerView;
    private ImageAdapter mImgAdapter;
    private GreedoLayoutManager mLayoutManager;

    private static ImgStore mImgStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = "";
        mImgStore = new ImgStore();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Main activity photo container
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mImgAdapter = new ImageAdapter(this, mImgStore, new PhotoOnClickListener());
        mLayoutManager = new GreedoLayoutManager(mImgAdapter);
        mLayoutManager.setMaxRowHeight(MeasUtils.dpToPx(150, this));

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mImgAdapter);

        int spacing = MeasUtils.dpToPx(4, this);
        mRecyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));


        // Fire Login activity to get OAuth pass.
        this.mPxApi = null;

        Intent logInIntent = new Intent(this, LoginActivity.class);
        MainActivity.this.startActivityForResult(logInIntent, LOGIN_ACTIVITY_INT);
    }

    // Configure onclick listener to fire detailed photo view when tapped.
    public class PhotoOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int idx = mRecyclerView.getChildLayoutPosition(v);

            Intent fullScreenIntent = new Intent(v.getContext(), FullScreenImgActivity.class);
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.INDEX), idx);
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.CACHE), mImgStore.getImgNames());
            fullScreenIntent.putExtra(MainActivity.class.getName() + getString(R.string.URL), mImgStore.getImgs());

            MainActivity.this.startActivityForResult(fullScreenIntent, IMG_DETAIL_ACTIVITY_INT);
        }
    }

    // Handle returned result from login activity and full screen photo activity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (LOGIN_ACTIVITY_INT) : {
                if (resultCode == Activity.RESULT_OK) {
                    mUsername = data.getStringExtra(getString(R.string.USERNAME));
                    mAccessToken = (AccessToken) data.getExtras().get((getString(R.string.ACCESS_TOKEN)));
                    this.mPxApi = new PxApi(this.mAccessToken, getString(R.string.consumer_key), getString(R.string.consumer_secret));

                    refreshImgStorage();
                }
                break;
            }
            case (IMG_DETAIL_ACTIVITY_INT) : {
                if (resultCode == Activity.RESULT_OK) {
                    int lastIdx = data.getIntExtra(getString(R.string.LAST_POSITION), 0);
                    int itemsCnt = mImgStore.size();
                    if (itemsCnt - lastIdx < NUMBER_OG_IMG_IN_ONE_SCREEN)
                        lastIdx = itemsCnt - NUMBER_OG_IMG_IN_ONE_SCREEN;
                    mLayoutManager.scrollToPosition(lastIdx);
                }
            }
        }
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.w(TAG, "success "+result);

    }

    @Override
    public void onFail(FiveHundredException e) {}

    // Helper function to refresh the photo container of the main page with up-to-date data.
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

            // Render to the main thread.
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
