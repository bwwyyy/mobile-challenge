package com.android.philip.photoapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;



public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.PhotoViewHolder> implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
    private static final String TAG = "ImageAdapter";

    private Context mContext;
    private ImgStore mCache;
    private MainActivity.PhotoOnClickListener mPhotoOnClickListener;

    public ImageAdapter(Context c, ImgStore cache, MainActivity.PhotoOnClickListener photoOnClickListener)  {
        Log.d(TAG, "Initialize");
        mContext = c;
        mCache = cache;
        mPhotoOnClickListener = photoOnClickListener;
    }

    @Override
    public double aspectRatioForIndex(int index) {
        return calculateImageAspectRatios(index);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setOnClickListener(mPhotoOnClickListener);
        return new PhotoViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        JSONObject currImg = mCache.getImageInfo(position);
        try {
            Picasso.with(mContext)
                    .load(currImg.getString("image_url"))
                    .into(holder.mImageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mCache.size();
    }

    private double calculateImageAspectRatios(int idx) {
        JSONObject currImg = mCache.getImageInfo(idx);
        if (currImg != null) {
            try {
                return currImg.getDouble("width") / currImg.getDouble("height");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 1.0;
    }


    public void updateDataSet (ImgStore cache) {
        mCache = cache;
    }
}
