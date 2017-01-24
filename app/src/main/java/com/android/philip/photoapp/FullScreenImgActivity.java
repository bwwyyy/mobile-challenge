package com.android.philip.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class FullScreenImgActivity extends AppCompatActivity {
    private static final String TAG = "FullScreenImgActivity";

    // Using View pager and SectionsPagerAdapter to set up a swipable view.
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static String [] mMemoryCache;
    private static String [] mImgCache;
    private static int mCurrIdx;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_image);
        Intent intent = getIntent();
        mCurrIdx = (int) intent.getExtras().get(MainActivity.class.getName() + getString(R.string.INDEX));
        mMemoryCache = (String[]) intent.getSerializableExtra(MainActivity.class.getName() + getString(R.string.CACHE));
        mImgCache = (String[]) intent.getSerializableExtra(MainActivity.class.getName() + getString(R.string.URL));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mCurrIdx);

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(getString(R.string.LAST_POSITION), mCurrIdx);
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String URL = "url";
        private static final String IMGNAME = "imgname";

        public PlaceholderFragment() {
        }

        //Returns a new instance of this fragment for the given section index.
        public static PlaceholderFragment newInstance(int index) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(URL, mImgCache[index]);

            args.putString(IMGNAME, mMemoryCache[index]);

            mCurrIdx = index;

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_swipe_img, container, false);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.fullImage);
            TextView imageInfoView = (TextView) rootView.findViewById(R.id.fullImageInfo);

            Picasso.with(this.getContext())
                    .load(getArguments().getString(URL))
                    .into(imageView);
            imageInfoView.setText(getArguments().getString(IMGNAME));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mMemoryCache.length;
        }
    }
}