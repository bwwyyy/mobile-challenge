package com.android.philip.photoapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

public class ImgStore implements Serializable {
    private static final String TAG = "ImgStore";

    // Image store to store the json information for each using image in Hash Table.
    private transient Hashtable<String, JSONObject> mImgInfo;
    private Hashtable<Integer, String> mIdxMap;

    // Keep track the # of img
    private AtomicInteger mSize;

    public ImgStore() {

        mSize = new AtomicInteger();
        mSize.set(0);

        mImgInfo = new Hashtable<>();
        mIdxMap = new Hashtable<>();
    }

    public int size () {
        return mSize.get();
    }

    public void addImage(String key, JSONObject info) {
        if (getImage(key) == null) {
            mImgInfo.put(key, info);
            mIdxMap.put(mSize.getAndAdd(1), key);
        }
    }

    public JSONObject getImage(String key) {
        return mImgInfo.get(key);
    }


    public JSONObject getImageInfo(String key) {
        return mImgInfo.get(key);
    }


    public JSONObject getImageInfo(int idx) {
        // using idx as initial Key to find the Key of photo json information.
        if (mIdxMap.containsKey(idx)) {
            return getImageInfo(mIdxMap.get(idx));
        }
        return null;
    }

    public String [] getImgNames () {
        //List<String> res = new ArrayList<>();
        String [] res = new String[mSize.get()];
        for (int i = 0; i < mSize.get(); ++ i) {
            try {
                res[i] = getImageInfo(i).getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public String [] getImgs () {
        String [] res = new String[mSize.get()];
        for (int i = 0; i < mSize.get(); ++ i) {
            try {
                res[i] = getImageInfo(i).getString("image_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return res;
    }

    public long getId(int idx) {
        if (mIdxMap.containsKey(idx)) {
            return Long.valueOf (mIdxMap.get(idx));
        }
        return -1;
    }
}
